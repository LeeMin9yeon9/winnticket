package kr.co.winnticket.common.enums;

public enum PartnerStatus {
    ACTIVE ("활성"),
    INACTIVE ("비활성"),
    PENDING ("대기중"),
    SUSPENDED ("정지");

    private final String lable;

    PartnerStatus(String lable) {
        this.lable = lable;
    }
    public String getLable() {
        return lable;
    }
}
