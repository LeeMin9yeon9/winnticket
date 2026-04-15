package kr.co.winnticket.integration.tosspayments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.tosspayments.dto.TossCancelResult;
import kr.co.winnticket.integration.tosspayments.dto.TossPaymentResDto;
import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.winnticket.common.enums.PaymentMethod;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Toss Payments 결제 비즈니스 로직 서비스
 * 결제 승인, 취소 처리 담당
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class TossPaymentsService {

    private final TossPaymentsClient tossClient;
    private final OrderShopMapper orderShopMapper;
    private final OrderMapper orderAdminMapper;
    private final ObjectMapper objectMapper;

    /**
     * 결제 승인 처리
     * FE가 Toss successUrl에서 받은 파라미터를 전달하면 최종 승인
     *
     * 흐름:
     * 1. DB에서 주문 금액 조회 후 위변조 검증
     * 2. Toss API confirm 호출
     * 3. DB에 paymentKey 저장 (나중에 취소 시 사용)
     * 4. 호출자(Controller)가 orderService.completePayment() 호출
     */
    @Transactional
    public TossPaymentResDto confirmPayment(String paymentKey, UUID orderId, Integer amount) {

        if (paymentKey == null || paymentKey.isBlank()) throw new IllegalArgumentException("paymentKey 없음");
        if (orderId == null) throw new IllegalArgumentException("orderId 없음");
        if (amount == null || amount <= 0) throw new IllegalArgumentException("amount 오류");

        // DB에서 주문 정보 조회
        Map<String, Object> orderInfo = orderAdminMapper.selectOrderPaymentInfo(orderId);
        if (orderInfo == null) throw new IllegalStateException("주문 없음 orderId=" + orderId);

        // pg_provider 검증
        String pgProvider = (String) orderInfo.get("pg_provider");
        if (pgProvider != null && !"TOSSPAYMENTS".equalsIgnoreCase(pgProvider)) {
            log.warn("[TOSS] confirm - pg_provider 불일치: {}", pgProvider);
        }

        // 금액 위변조 검증 (finalPrice - pointAmount = PG 실 결제금액)
        int finalPrice = ((Number) orderInfo.get("final_price")).intValue();
        int pointAmount = orderAdminMapper.selectPointAmount(orderId);
        int expectedPgAmount = finalPrice - pointAmount;

        if (!amount.equals(expectedPgAmount)) {
            throw new IllegalStateException(
                    "결제 금액 불일치 - 요청금액=" + amount + ", 예상금액=" + expectedPgAmount
            );
        }

        // Toss API 승인 호출
        TossPaymentResDto result = tossClient.confirm(paymentKey, orderId.toString(), amount);

        if (result == null) {
            throw new IllegalStateException("[TOSS] confirm 응답 null");
        }

        if (result.isFailed()) {
            throw new IllegalStateException("[TOSS] confirm 실패 code=" + result.getCode() + ", msg=" + result.getMessage());
        }

        if (!"DONE".equals(result.getStatus())) {
            throw new IllegalStateException("[TOSS] 결제 상태 이상: " + result.getStatus());
        }

        // DB에 paymentKey 저장 (취소 시 필요)
        String payloadJson = null;
        try {
            payloadJson = objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.warn("[TOSS] payload JSON 변환 실패", e);
        }

        int updated = orderShopMapper.updateTossConfirmSuccessIfNotPaid(orderId, paymentKey, payloadJson);

        if (updated == 0) {
            log.info("[TOSS] 이미 처리된 결제 orderId={}", orderId);
        }

        // 토스 응답의 실제 결제수단(method)을 DB payment_method에 반영
        // 예: "카드" → CARD, "가상계좌" → VIRTUAL_ACCOUNT, "계좌이체" → TRANSFER
        if (result.getMethod() != null) {
            PaymentMethod actualMethod = PaymentMethod.fromTossMethod(result.getMethod());
            orderShopMapper.updatePaymentMethod(orderId, actualMethod.name());
            log.info("[TOSS] 결제수단 업데이트 orderId={}, tossMethod={}, dbMethod={}",
                    orderId, result.getMethod(), actualMethod.name());
        }

        log.info("[TOSS] confirmPayment 완료 orderId={}, paymentKey={}", orderId, paymentKey);
        return result;
    }

    /**
     * 결제 취소 (관리자 주문 취소 시 호출)
     * OrderService.cancelOrder()에서 CARD/KAKAOPAY 결제 방식일 때 호출됨
     *
     * 취소 수수료 정책:
     * - 예약 상품: 전액 환불
     * - 일반 상품 7일 이하: 1,000원 수수료
     * - 일반 상품 7일 초과: 결제금액의 10% 수수료
     */
    @Transactional
    public TossCancelResult cancel(UUID orderId) {

        if (orderId == null) throw new IllegalArgumentException("orderId 없음");

        Map<String, Object> orderInfo = orderAdminMapper.selectOrderPaymentInfo(orderId);
        if (orderInfo == null) throw new IllegalStateException("주문 없음 orderId=" + orderId);

        // TOSSPAYMENTS 주문 검증
        String pgProvider = (String) orderInfo.get("pg_provider");
        if (!"TOSSPAYMENTS".equalsIgnoreCase(pgProvider)) {
            throw new IllegalStateException("TOSSPAYMENTS 주문 아님: pg_provider=" + pgProvider);
        }

        // paymentKey 조회 (confirm 시 pg_tid에 저장)
        String paymentKey = (String) orderInfo.get("pg_tid");
        if (paymentKey == null || paymentKey.isBlank()) {
            throw new IllegalStateException("paymentKey 없음 (pg_tid 미설정) orderId=" + orderId);
        }

        String orderNumber = (String) orderInfo.get("order_number");

        // 취소 가능 금액 계산
        int finalPrice = ((Number) orderInfo.get("final_price")).intValue();
        int alreadyCanceled = orderInfo.get("cancel_amount") != null
                ? ((Number) orderInfo.get("cancel_amount")).intValue()
                : 0;
        int remainAmount = finalPrice - alreadyCanceled;

        if (remainAmount <= 0) {
            throw new IllegalStateException("이미 전액 취소된 주문 orderId=" + orderId);
        }

        // 예약 상품 여부 확인
        List<OrderProductListGetResDto> items = orderAdminMapper.selectOrderProductList(orderId);
        boolean isReservation = items.stream()
                .anyMatch(item -> Boolean.TRUE.equals(item.getIsReservation()));

        int cancelAmount;
        int cancelFee = 0;

        if (isReservation) {
            // 예약 상품: 전액 환불
            cancelAmount = remainAmount;
            log.info("[예약상품] 전액환불 orderId={}, amount={}", orderId, cancelAmount);
        } else {
            // 일반 상품: 경과일 기준 수수료 적용
            Timestamp ts = (Timestamp) orderInfo.get("ordered_at");
            LocalDateTime orderedAt = ts.toLocalDateTime();
            long days = ChronoUnit.DAYS.between(orderedAt.toLocalDate(), LocalDate.now());

            cancelFee = (days <= 7) ? 1000 : (int) Math.floor(remainAmount * 0.1);
            cancelAmount = Math.max(remainAmount - cancelFee, 0);

            log.info("[일반상품] orderId={}, days={}, fee={}, refund={}", orderId, days, cancelFee, cancelAmount);
        }

        // Toss API 취소 호출
        TossPaymentResDto result = tossClient.cancel(paymentKey, "관리자 취소", cancelAmount);

        if (result == null) {
            throw new IllegalStateException("[TOSS] cancel 응답 null");
        }

        if (result.isFailed()) {
            throw new IllegalStateException("[TOSS] cancel 실패 code=" + result.getCode() + ", msg=" + result.getMessage());
        }

        // DB 취소 내역 저장
        try {
            Map<String, Object> cancelPayload = Map.of(
                    "cancelAmount", cancelAmount,
                    "cancelFee", cancelFee,
                    "paymentKey", paymentKey,
                    "status", result.getStatus() != null ? result.getStatus() : "UNKNOWN"
            );
            String payloadJson = objectMapper.writeValueAsString(cancelPayload);
            orderAdminMapper.updateOrderCancelSuccess(orderId, cancelAmount, cancelFee, payloadJson);
        } catch (Exception e) {
            log.error("[TOSS] cancel payload 저장 실패 orderId={}", orderId, e);
        }

        log.info("[TOSS CANCEL SUCCESS] orderId={}, refund={}, fee={}", orderId, cancelAmount, cancelFee);
        return new TossCancelResult(cancelAmount, cancelFee, result);
    }
}
