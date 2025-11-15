package kr.co.winnticket.common.enums;

public enum PartnerType {
    VENUE ("공연장"),
    PROMOTER("주최사"),
    AGENCY ("기획사"),
    ARTIST ("아티스트"),
    CORPORATE ("기업");

    private final String label;
    PartnerType(String label){
        this.label = label;
    }
    public String getLabel() {
        return label;
    }
}
