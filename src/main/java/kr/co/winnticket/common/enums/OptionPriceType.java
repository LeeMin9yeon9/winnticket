package kr.co.winnticket.common.enums;

public enum OptionPriceType {
    ADDITIONAL("기존금액에 추가"),
    OVERRIDE("추가금액만");

    private final String displayName;

    OptionPriceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
