package kr.co.winnticket.integration.tosspayments.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Toss Payments API 결제 응답 객체
 * 승인/취소 API 공통 응답
 * 불필요한 필드는 @JsonIgnoreProperties로 무시
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TossPaymentResDto {

    /** Toss 결제 키 (결제 취소 시 식별자로 사용) */
    private String paymentKey;

    /** 주문 ID (우리 order UUID) */
    private String orderId;

    /** 주문명 */
    private String orderName;

    /**
     * 결제 상태
     * READY: 결제 준비
     * IN_PROGRESS: 결제 진행 중
     * WAITING_FOR_DEPOSIT: 가상계좌 입금 대기
     * DONE: 결제 완료
     * CANCELED: 전액 취소
     * PARTIAL_CANCELED: 부분 취소
     * ABORTED: 결제 승인 실패
     * EXPIRED: 만료
     */
    private String status;

    /** 총 결제 금액 */
    private Integer totalAmount;

    /** 결제 수단 (카드, 가상계좌, 간편결제, 휴대폰, 계좌이체 등) */
    private String method;

    /** 결제 요청 일시 (ISO 8601) */
    private String requestedAt;

    /** 결제 승인 일시 (ISO 8601) */
    private String approvedAt;

    /** 간편결제 정보 (method가 "간편결제"일 때) */
    private EasyPay easyPay;

    /** 에러 코드 (실패 시) */
    private Integer code;

    /** 에러 메시지 (실패 시) */
    private String message;

    /**
     * 간편결제 상세 정보
     * provider: "토스페이", "카카오페이", "네이버페이" 등
     */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EasyPay {
        private String provider;
        private Integer amount;
        private Integer discountAmount;
    }

    /**
     * 어드민 표시용 결제수단 문자열
     * 예: "카드(토스)", "가상계좌(토스)", "카카오페이(토스)", "토스페이"
     */
    public String getDisplayMethod() {
        if ("간편결제".equals(method) && easyPay != null && easyPay.getProvider() != null) {
            return easyPay.getProvider(); // "토스페이", "카카오페이" 등
        }
        return method; // "카드", "가상계좌" 등
    }

    /** 결제가 성공(DONE)인지 확인 */
    public boolean isSuccess() {
        return "DONE".equals(status);
    }

    /** 결제가 실패했는지 확인 */
    public boolean isFailed() {
        return code != null && code != 0;
    }
}
