package kr.co.winnticket.common.enums;

public enum PaymentMethod {
    CARD("카드"),
    VIRTUAL_ACCOUNT("무통장입금"),
    POINT("베네피아 포인트"),
    GIFT("베네피아 상품권"),
    KAKAOPAY("카카오페이");

    /**
     * 토스 결제 응답의 method 문자열을 PaymentMethod enum으로 변환
     * 예: "카드" → CARD, "가상계좌" → VIRTUAL_ACCOUNT
     * DB payment_method enum에 맞게 매핑 (계좌이체/간편결제 등은 CARD로 매핑)
     */
    public static PaymentMethod fromTossMethod(String tossMethod) {
        if (tossMethod == null) return CARD;
        return switch (tossMethod) {
            case "카드" -> CARD;
            case "가상계좌" -> VIRTUAL_ACCOUNT;
            case "간편결제" -> CARD; // 토스페이 등 간편결제는 카드로 분류
            default -> CARD; // 계좌이체 등은 CARD로 매핑
        };
    }



    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
