package kr.co.winnticket.common.enums;

public enum ProductType {
    NORMAL("일반"),
    STAY("숙박");

    private final String displayName;

    ProductType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

