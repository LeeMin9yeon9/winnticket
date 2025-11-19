package kr.co.winnticket.common.enums;

public enum FaqCategory {
    ORDER("주문/배송관리"),
    DELIVERY("배송"),
    CANCEL("취소/환불"),
    TICKET("티켓"),
    MEMBERSHIP("회원"),
    ETC("기타");

    private final String displayName;

    FaqCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
