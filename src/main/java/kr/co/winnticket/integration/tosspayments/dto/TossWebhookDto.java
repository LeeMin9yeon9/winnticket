package kr.co.winnticket.integration.tosspayments.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Toss Payments 웹훅 수신 DTO
 * Toss에서 서버-to-서버로 전송하는 이벤트 알림
 *
 * 주요 이벤트 타입:
 * - PAYMENT_STATUS_CHANGED: 결제 상태 변경 (카드 결제 완료/취소 등)
 * - DEPOSIT_CALLBACK: 가상계좌 입금 완료 알림
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TossWebhookDto {

    /** 이벤트 타입 (PAYMENT_STATUS_CHANGED, DEPOSIT_CALLBACK) */
    private String eventType;

    /** 이벤트 생성 일시 */
    private String createdAt;

    /** 이벤트 데이터 */
    private Data data;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {

        /** Toss 결제 키 */
        private String paymentKey;

        /** 주문 ID (우리 order UUID) */
        private String orderId;

        /** 변경된 결제 상태 */
        private String status;

        /** 결제 금액 */
        private Integer totalAmount;
    }
}
