package kr.co.winnticket.integration.tosspayments.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * FE → BE 결제 승인 요청 DTO
 * Toss 결제 성공 후 FE가 successUrl로 리다이렉트 받은 파라미터를 전달
 */
@Getter
@Setter
public class TossConfirmReqDto {

    /** Toss가 발급한 결제 키 (successUrl 파라미터로 전달됨) */
    private String paymentKey;

    /** 우리 주문 UUID (requestPayment 시 orderId로 넘긴 값) */
    private String orderId;

    /** 실제 결제 금액 (위변조 방지 서버 검증용) */
    private Integer amount;
}
