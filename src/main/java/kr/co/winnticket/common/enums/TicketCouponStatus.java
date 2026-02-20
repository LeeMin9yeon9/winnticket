package kr.co.winnticket.common.enums;

public enum TicketCouponStatus {
    ACTIVE("쿠폰 사용 가능"),
    USED("쿠폰 사용 완료"),
    EXPIRED("쿠폰 기간 만료"),
    CANCELLED("쿠폰 사용 취소") ;

    private final String displayName;

    TicketCouponStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
