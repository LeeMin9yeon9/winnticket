package kr.co.winnticket.common.enums;

public enum PaymentMethod {
    CARD("카드"),
    VIRTUAL_ACCOUNT("무통장입금"),
    POINT("베네피아 포인트");



    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
