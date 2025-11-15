package kr.co.winnticket.common.enums;

public enum DiscountType {
    PERCENTAGE ("퍼센트"),
    FIXED ("고정금액"),
    BUNDLE ("묶음할인");

    private final String lable;

    DiscountType(String lable) {
        this.lable = lable;
    }

    public String getLable() {
        return lable;
    }
}
