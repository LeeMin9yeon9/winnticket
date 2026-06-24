package kr.co.winnticket.order.admin.service;

import kr.co.winnticket.common.enums.ProductType;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointCancelReqDto;
import kr.co.winnticket.integration.benepia.kcp.service.BenepiaCredentialStore;
import kr.co.winnticket.integration.benepia.kcp.service.KcpService;
import kr.co.winnticket.integration.tosspayments.service.TossPaymentsService;
import kr.co.winnticket.order.admin.dto.OrderItemOptionDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import kr.co.winnticket.ticketCoupon.service.TicketCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 주문 취소/실패 시 후처리(재고/쿠폰/포인트/카드 환불, Redis 정리, 상태전환)를
 * 한 곳에서 처리하는 서비스.
 *
 * 호출자는 필요한 단계만 골라 부르거나, 통합 메서드(cleanupRequestedOrder)를 호출한다.
 *
 * 각 단계는 독립적으로 try/catch — 한 단계 실패가 다음 단계를 막지 않는다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCleanupService {

    private final OrderMapper orderMapper;
    private final OrderShopMapper orderShopMapper;
    private final KcpService kcpService;
    private final TossPaymentsService tossPaymentsService;
    private final TicketCouponService ticketCouponService;
    private final BenepiaCredentialStore benepiaCredentialStore;

    /**
     * REQUESTED 상태인 주문을 atomic 하게 FAILED/CANCELED 로 전환.
     * @return 전환 성공 시 true (다른 상태였으면 false)
     */
    public boolean cancelToFailedIfRequested(UUID orderId) {
        int updated = orderShopMapper.updateCancelIfRequested(orderId);
        if (updated > 0) {
            log.info("[CLEANUP] 주문 FAILED/CANCELED 전환 orderId={}", orderId);
            return true;
        }
        log.info("[CLEANUP] 상태 전환 스킵 (이미 다른 상태) orderId={}", orderId);
        return false;
    }

    /**
     * 재고 복구. STAY 타입은 제외.
     */
    public void restoreStock(UUID orderId) {
        try {
            List<OrderItemOptionDto> options = orderMapper.selectOrderItemOptions(orderId);
            for (OrderItemOptionDto opt : options) {
                if (!ProductType.STAY.equals(opt.getProductType())) {
                    orderMapper.increaseStock(opt.getOptionValueId(), opt.getQuantity());
                }
            }
            log.info("[CLEANUP] 재고 복구 완료 orderId={}", orderId);
        } catch (Exception e) {
            log.error("[CLEANUP] 재고 복구 실패 orderId={}", orderId, e);
        }
    }

    /**
     * 예약쿠폰 복구.
     */
    public void restoreCoupons(UUID orderId) {
        try {
            ticketCouponService.restoreReservedCoupons(orderId);
            log.info("[CLEANUP] 쿠폰 복구 완료 orderId={}", orderId);
        } catch (Exception e) {
            log.error("[CLEANUP] 쿠폰 복구 실패 orderId={}", orderId, e);
        }
    }

    /**
     * point_tid 가 존재하면 KCP 포인트 전액 환불(STSC).
     */
    public void refundPointIfCharged(UUID orderId, String reason) {
        try {
            String orderNumber = orderMapper.findOrderNumberById(orderId);
            if (orderNumber == null) {
                log.warn("[CLEANUP] orderNumber 없음 orderId={}", orderId);
                return;
            }
            String pointTid = orderMapper.selectPointTno(orderNumber);
            if (pointTid == null || pointTid.isBlank()) {
                log.info("[CLEANUP] 포인트 환불 스킵 (point_tid 없음) orderId={}", orderId);
                return;
            }

            KcpPointCancelReqDto dto = new KcpPointCancelReqDto();
            dto.setTno(pointTid);
            dto.setCancelReason(reason);
            kcpService.cancelPoint(dto);

            log.info("[CLEANUP] 포인트 환불 완료 orderId={}", orderId);
        } catch (Exception e) {
            log.error("[CLEANUP] 포인트 환불 실패 orderId={}", orderId, e);
        }
    }

    /**
     * 카드가 실제 결제된 경우 수수료 없이 전액 환불.
     * 결제가 완료되지 않아 paymentKey(pg_tid)가 없는 주문(결제창 중도 이탈/실패 등)은
     * 환불할 결제 자체가 없으므로 조용히 스킵한다. (불필요한 ERROR 로그 방지)
     */
    public void refundCardWithoutFee(UUID orderId) {
        try {
            // 실제 결제 완료(paymentKey 존재) 여부 선검사 - 없으면 환불 대상 아님
            Map<String, Object> orderInfo = orderMapper.selectOrderPaymentInfo(orderId);
            String paymentKey = orderInfo != null ? (String) orderInfo.get("pg_tid") : null;
            if (paymentKey == null || paymentKey.isBlank()) {
                log.info("[CLEANUP] 카드 환불 스킵 (결제 미완료, paymentKey 없음) orderId={}", orderId);
                return;
            }

            tossPaymentsService.cancel(orderId);
            log.info("[CLEANUP] 카드 환불 완료 orderId={}", orderId);
        } catch (Exception e) {
            log.error("[CLEANUP] 카드 환불 실패 orderId={}", orderId, e);
        }
    }

    /**
     * 베네피아 인증정보 임시 저장소 삭제.
     */
    public void clearBenepiaCredential(UUID orderId) {
        try {
            benepiaCredentialStore.delete(orderId);
        } catch (Exception e) {
            log.error("[CLEANUP] 베네피아 인증정보 삭제 실패 orderId={}", orderId, e);
        }
    }

    /**
     * REQUESTED 상태 주문에 대한 통합 정리.
     * 1) 상태전환(REQUESTED→FAILED/CANCELED)
     * 2) 포인트 환불 (point_tid 있을 때)
     * 3) Redis 인증정보 삭제
     * 4) 카드 환불 (card_amount > 0 일 때 수수료 없이)
     * 5) 재고 복구
     * 6) 쿠폰 복구
     *
     * @return 1단계 성공 여부 (이미 PAID 등이면 false 반환, 이후 단계 모두 스킵)
     */
    public boolean cleanupRequestedOrder(UUID orderId, String reason) {
        if (!cancelToFailedIfRequested(orderId)) {
            return false;
        }
        refundPointIfCharged(orderId, reason);
        clearBenepiaCredential(orderId);
        refundCardWithoutFee(orderId);
        restoreStock(orderId);
        restoreCoupons(orderId);
        return true;
    }
}
