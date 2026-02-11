package kr.co.winnticket.common.enums;

public enum PartnerStatus {
    ACTIVE ("활성",1),
    INACTIVE ("비활성",2),
    DELETED ("삭제",3);

    private final String label;
    private final int order;

    PartnerStatus(String label, int order) {
        this.label = label;
        this.order = order;
    }
    public String getLabel() {
        return label;
    }
    public int getOrder() {
        return order;
    }
}
