package kr.co.winnticket.common.enums;

public enum SalesStatus {
    READY("준비중"),
    ON_SALE("판매중"),
    SOLD_OUT("품절"),
    PAUSED("판매중단"),
    ENDED("판매종료");

    private final String displayName;

    SalesStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
