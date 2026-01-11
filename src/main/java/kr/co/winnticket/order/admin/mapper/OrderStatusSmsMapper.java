package kr.co.winnticket.order.admin.mapper;

import kr.co.winnticket.common.enums.OrderStatus;
import kr.co.winnticket.common.enums.SmsTemplateCode;

import java.util.List;

import static kr.co.winnticket.common.enums.OrderStatus.PENDING_PAYMENT;

public class OrderStatusSmsMapper {

    public static List<SmsTemplateCode> map(OrderStatus status) {

        return switch (status) {

            // 주문이 처음 생성되었을 때
            case PENDING_PAYMENT -> List.of(SmsTemplateCode.ORDER_RECEIVED);

            // 관리자가 입금확인 처리 완료했을 때
            case COMPLETED -> List.of(
                    SmsTemplateCode.PAYMENT_CONFIRMED,
                    SmsTemplateCode.TICKET_ISSUED
            );

            // 취소 승인 완료
            case CANCELED -> List.of(SmsTemplateCode.ORDER_CANCELLED);

            // 환불 완료 지금은 문자 템플릿없음
            case REFUNDED -> null;

            // 취소요청 단계는 문자 발송 안 함
            case CANCEL_REQUESTED -> null;
        };
    }
}
