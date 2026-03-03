package kr.co.winnticket.common.enums;

public enum PartnerType {
    FIELD_MANAGER ("현장관리자"),
    PARTNER ("연동처");

    private final String label;
    PartnerType(String label){
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
}
