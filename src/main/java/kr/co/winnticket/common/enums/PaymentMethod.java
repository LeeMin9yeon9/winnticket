package kr.co.winnticket.common.enums;

public enum PaymentMethod {
    CARD("카드"),
    BANK_TRANSFER("계좌이체"),
    EASY_PAY("간편결제"),
    VIRTUAL_ACCOUNT("무통장입금");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
