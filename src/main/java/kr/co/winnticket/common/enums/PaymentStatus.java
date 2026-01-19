package kr.co.winnticket.common.enums;

public enum PaymentStatus {
    READY("입금대기"),
    PAID("결제완료"),
    FAILED("결제실패"),
    CANCELED("취소완료"),
    REQUESTED("PG에 결제 요청함"),
    REFUNDED("환불완료");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
