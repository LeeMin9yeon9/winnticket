package kr.co.winnticket.integration.mair.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
// 주문정보 DTO (orders 테이블에서 발송에 필요한 정보 가져오기)
public class MairOrderInfoDto {
    private UUID orderId;
    private String orderNumber;  // 주문번호
    private String customerName;  // 주문자
    private String customerPhone;  // 주문자핸드폰
    private String paymentStatus;  // 결제상태
}
