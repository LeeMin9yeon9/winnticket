package kr.co.winnticket.common.enums;

public enum OrderStatus {
    PENDING_PAYMENT("입금전"),
    COMPLETED("주문처리완료"),
    CANCEL_REQUESTED("취소신청"),
    CANCELED("취소완료"),
    REFUNDED("환불완료");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
