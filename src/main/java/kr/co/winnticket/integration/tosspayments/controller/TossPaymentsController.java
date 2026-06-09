package kr.co.winnticket.integration.tosspayments.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.integration.tosspayments.dto.TossConfirmReqDto;
import kr.co.winnticket.integration.tosspayments.dto.TossPaymentResDto;
import kr.co.winnticket.integration.tosspayments.dto.TossWebhookDto;
import kr.co.winnticket.integration.tosspayments.service.TossPaymentsService;
import kr.co.winnticket.order.admin.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Toss Payments 연동 컨트롤러
 * FE 결제 승인 요청 및 Toss 웹훅 처리
 */
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/toss")
@Tag(name = "Toss Payments", description = "토스 페이먼츠 결제 연동 API")
public class TossPaymentsController {

    private final TossPaymentsService tossPaymentsService;
    private final OrderService orderService;

    /**
     * 결제 승인 엔드포인트
     * FE가 Toss successUrl 리다이렉트 후 paymentKey/orderId/amount를 전달하면 최종 승인 처리
     *
     * 처리 순서:
     * 1. 금액 위변조 검증 + Toss API 승인
     * 2. 티켓 발급 + 파트너 연동 + SMS 발송 (completePayment)
     */
    @PostMapping("/confirm")
    @Operation(summary = "Toss 결제 승인", description = "FE에서 결제 완료 후 최종 승인 요청")
    public ResponseEntity<ApiResponse<String>> confirm(@RequestBody TossConfirmReqDto req) {

        log.info("[TOSS] confirm 요청 orderId={}, amount={}", req.getOrderId(), req.getAmount());

        try {
            UUID orderId = UUID.fromString(req.getOrderId());

            // Toss API 승인 + DB paymentKey 저장
            TossPaymentResDto result = tossPaymentsService.confirmPayment(
                    req.getPaymentKey(),
                    orderId,
                    req.getAmount()
            );

            // 티켓 발급 + 파트너 연동 + SMS
            orderService.completePayment(orderId);

            log.info("[TOSS] confirm 완료 orderId={}", orderId);
            return ResponseEntity.ok(ApiResponse.success("결제 완료", orderId.toString()));

        } catch (Exception e) {
            log.error("[TOSS] confirm 실패 orderId={}", req.getOrderId(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("결제 승인 실패: " + e.getMessage()));
        }
    }

    /**
     * Toss 웹훅 수신 엔드포인트
     * Toss에서 서버-to-서버로 전송하는 결제 상태 변경 이벤트 처리
     *
     * 주요 이벤트:
     * - PAYMENT_STATUS_CHANGED: 카드 승인/취소
     * - DEPOSIT_CALLBACK: 가상계좌 입금 완료 (추후 활용 가능)
     */
    @PostMapping("/webhook")
    @Operation(summary = "Toss 웹훅 수신", description = "Toss에서 발송하는 결제 상태 변경 이벤트 수신")
    public ResponseEntity<Map<String, Object>> webhook(@RequestBody TossWebhookDto payload) {

        log.info("[TOSS WEBHOOK] eventType={}, orderId={}, status={}",
                payload.getEventType(),
                payload.getData() != null ? payload.getData().getOrderId() : "N/A",
                payload.getData() != null ? payload.getData().getStatus() : "N/A"
        );

        try {
            // DEPOSIT_CALLBACK: 가상계좌 입금 완료 시 자동 결제 완료 처리
            if ("DEPOSIT_CALLBACK".equals(payload.getEventType())
                    && payload.getData() != null
                    && "DONE".equals(payload.getData().getStatus())) {

                String orderIdStr = payload.getData().getOrderId();
                if (orderIdStr != null && !orderIdStr.isBlank()) {
                    UUID orderId = UUID.fromString(orderIdStr);
                    orderService.completePayment(orderId);
                    log.info("[TOSS WEBHOOK] 가상계좌 입금 완료 처리 orderId={}", orderId);
                }
            }

            // Toss 웹훅은 HTTP 200 응답을 요구
            return ResponseEntity.ok(Map.of("code", 0, "message", "success"));

        } catch (Exception e) {
            log.error("[TOSS WEBHOOK] 처리 실패", e);
            // 웹훅은 실패해도 200 반환 (Toss가 재전송하지 않도록)
            return ResponseEntity.ok(Map.of("code", 1, "message", "처리 중 오류 발생"));
        }
    }
}
