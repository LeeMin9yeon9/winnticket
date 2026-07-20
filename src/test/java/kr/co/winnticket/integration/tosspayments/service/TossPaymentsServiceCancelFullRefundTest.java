package kr.co.winnticket.integration.tosspayments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.tosspayments.dto.TossCancelResult;
import kr.co.winnticket.integration.tosspayments.dto.TossPaymentResDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * TossPaymentsService.cancelFullRefund 단위 테스트
 *
 * 핵심 검증: 토스는 카드 분담금(final_price - point_amount)만 받았으므로
 * 환불도 그 금액만 해야 한다(혼합결제 과다환불 방지).
 */
@ExtendWith(MockitoExtension.class)
class TossPaymentsServiceCancelFullRefundTest {

    @Mock private TossPaymentsClient tossClient;
    @Mock private OrderShopMapper orderShopMapper;
    @Mock private OrderMapper orderAdminMapper;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks private TossPaymentsService service;

    private Map<String, Object> orderInfo(String pgProvider, String pgTid,
                                          Integer finalPrice, Integer pointAmount, Integer cancelAmount) {
        Map<String, Object> m = new HashMap<>();
        m.put("pg_provider", pgProvider);
        m.put("pg_tid", pgTid);
        m.put("final_price", finalPrice);
        m.put("point_amount", pointAmount);
        m.put("cancel_amount", cancelAmount);
        return m;
    }

    private TossPaymentResDto doneResult() {
        TossPaymentResDto r = new TossPaymentResDto();
        r.setStatus("CANCELED");
        return r; // code=null → isFailed()=false
    }

    @Test
    @DisplayName("혼합결제: 카드 분담금(final-point)만 환불한다")
    void hybrid_refundsOnlyCardPortion() {
        UUID orderId = UUID.randomUUID();
        when(orderAdminMapper.selectOrderPaymentInfo(orderId))
                .thenReturn(orderInfo("TOSSPAYMENTS", "pk_1", 10000, 3000, 0));
        when(tossClient.cancel(eq("pk_1"), any(), eq(7000))).thenReturn(doneResult());

        TossCancelResult result = service.cancelFullRefund(orderId, "보상");

        verify(tossClient).cancel(eq("pk_1"), any(), eq(7000));
        assertThat(result.getCancelAmount()).isEqualTo(7000);
        assertThat(result.getCancelFee()).isZero();
    }

    @Test
    @DisplayName("순수 카드결제: final_price 전액 환불")
    void cardOnly_refundsFullPrice() {
        UUID orderId = UUID.randomUUID();
        when(orderAdminMapper.selectOrderPaymentInfo(orderId))
                .thenReturn(orderInfo("TOSSPAYMENTS", "pk_2", 5000, 0, 0));
        when(tossClient.cancel(eq("pk_2"), any(), eq(5000))).thenReturn(doneResult());

        TossCancelResult result = service.cancelFullRefund(orderId, "보상");

        verify(tossClient).cancel(eq("pk_2"), any(), eq(5000));
        assertThat(result.getCancelAmount()).isEqualTo(5000);
    }

    @Test
    @DisplayName("paymentKey 없음(결제 미완료) → 토스 호출 없이 스킵")
    void noPaymentKey_skips() {
        UUID orderId = UUID.randomUUID();
        when(orderAdminMapper.selectOrderPaymentInfo(orderId))
                .thenReturn(orderInfo("TOSSPAYMENTS", null, 10000, 0, 0));

        TossCancelResult result = service.cancelFullRefund(orderId, "보상");

        verify(tossClient, never()).cancel(any(), any(), any());
        assertThat(result.getCancelAmount()).isZero();
    }

    @Test
    @DisplayName("토스 주문 아님 → 스킵")
    void notTossOrder_skips() {
        UUID orderId = UUID.randomUUID();
        when(orderAdminMapper.selectOrderPaymentInfo(orderId))
                .thenReturn(orderInfo("PAYLETTER", "pk_x", 10000, 0, 0));

        service.cancelFullRefund(orderId, "보상");

        verify(tossClient, never()).cancel(any(), any(), any());
    }

    @Test
    @DisplayName("이미 전액 취소됨(잔여 0) → 스킵")
    void alreadyFullyCanceled_skips() {
        UUID orderId = UUID.randomUUID();
        when(orderAdminMapper.selectOrderPaymentInfo(orderId))
                .thenReturn(orderInfo("TOSSPAYMENTS", "pk_3", 10000, 0, 10000));

        TossCancelResult result = service.cancelFullRefund(orderId, "보상");

        verify(tossClient, never()).cancel(any(), any(), any());
        assertThat(result.getCancelAmount()).isZero();
    }
}
