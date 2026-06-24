package kr.co.winnticket.order.admin.service;

import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointCancelReqDto;
import kr.co.winnticket.integration.benepia.kcp.service.KcpService;
import kr.co.winnticket.integration.tosspayments.service.TossPaymentsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * OrderService.compensateFailedPayment 단위 테스트
 *
 * 결제 후처리 실패 시:
 * - 카드(토스)는 항상 환불 시도
 * - 포인트는 tno가 있을 때만 환불
 * - 주문 실패처리 + 재고/쿠폰 복구는 항상 수행
 * - 일부 환불이 실패해도 나머지 보상은 계속 진행(resilient)
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceCompensationTest {

    @Mock private TossPaymentsService tossPaymentsService;
    @Mock private KcpService kcpService;
    @Mock private OrderCleanupService orderCleanupService;

    @InjectMocks private OrderService orderService;

    @Test
    @DisplayName("혼합결제 보상: 카드 환불 + 포인트 환불 + 정리 모두 수행")
    void hybrid_refundsCardAndPointAndCleansUp() {
        UUID orderId = UUID.randomUUID();

        orderService.compensateFailedPayment(orderId, "kcp_tno_123");

        verify(tossPaymentsService).cancelFullRefund(eq(orderId), any());
        verify(kcpService).cancelPoint(any(KcpPointCancelReqDto.class));
        verify(orderCleanupService).cancelToFailedIfRequested(orderId);
        verify(orderCleanupService).restoreStock(orderId);
        verify(orderCleanupService).restoreCoupons(orderId);
    }

    @Test
    @DisplayName("순수 카드결제 보상: 포인트 tno 없으면 포인트 환불은 호출하지 않음")
    void cardOnly_noPointRefund() {
        UUID orderId = UUID.randomUUID();

        orderService.compensateFailedPayment(orderId, null);

        verify(tossPaymentsService).cancelFullRefund(eq(orderId), any());
        verify(kcpService, never()).cancelPoint(any());
        verify(orderCleanupService).cancelToFailedIfRequested(orderId);
        verify(orderCleanupService).restoreStock(orderId);
        verify(orderCleanupService).restoreCoupons(orderId);
    }

    @Test
    @DisplayName("카드 환불이 실패해도 포인트 환불과 정리는 계속 진행")
    void cardRefundFails_pointAndCleanupStillRun() {
        UUID orderId = UUID.randomUUID();
        doThrow(new RuntimeException("토스 오류"))
                .when(tossPaymentsService).cancelFullRefund(eq(orderId), any());

        orderService.compensateFailedPayment(orderId, "kcp_tno_999");

        verify(kcpService).cancelPoint(any(KcpPointCancelReqDto.class));
        verify(orderCleanupService).cancelToFailedIfRequested(orderId);
        verify(orderCleanupService).restoreStock(orderId);
        verify(orderCleanupService).restoreCoupons(orderId);
    }

    @Test
    @DisplayName("빈 문자열 tno → 포인트 환불 스킵")
    void blankTno_noPointRefund() {
        UUID orderId = UUID.randomUUID();

        orderService.compensateFailedPayment(orderId, "  ");

        verify(kcpService, never()).cancelPoint(any());
    }
}
