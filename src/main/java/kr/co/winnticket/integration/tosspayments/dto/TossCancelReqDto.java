package kr.co.winnticket.integration.tosspayments.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Toss Payments 결제 취소 요청 DTO
 * POST /v1/payments/{paymentKey}/cancel
 */
@Getter
@Setter
@Builder
public class TossCancelReqDto {

    /** 취소 사유 (필수) */
    private String cancelReason;

    /** 취소 금액 (null이면 전액 취소) */
    private Integer cancelAmount;

    /** 비과세 금액 */
    private Integer taxFreeAmount;

    /** 부가세 금액 */
    private Integer taxAmount;
}
