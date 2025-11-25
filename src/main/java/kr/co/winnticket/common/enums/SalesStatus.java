package kr.co.winnticket.common.enums;

public enum SalesStatus {
    READY("준비중"),
    ON_SALE("판매중"),
    SOLD_OUT("품절"),
    STOPPED("판매중단");

    private final String displayName;

    SalesStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
