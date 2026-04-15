package kr.co.winnticket.integration.tosspayments.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 결제 취소 처리 결과 (OrderService에서 사용)
 * 취소 금액, 취소 수수료, Toss 응답을 포함
 */
@Getter
@AllArgsConstructor
public class TossCancelResult {

    /** 실제 환불 금액 (취소금액 - 취소수수료) */
    private int cancelAmount;

    /** 취소 수수료 (7일 이하: 1000원, 그 이상: 결제금액의 10%) */
    private int cancelFee;

    /** Toss API 취소 응답 */
    private TossPaymentResDto pgResult;
}
