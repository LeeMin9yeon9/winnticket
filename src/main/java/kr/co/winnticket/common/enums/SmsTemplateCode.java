package kr.co.winnticket.common.enums;

public enum SmsTemplateCode {
    ORDER_RECEIVED("주문접수"),
    PAYMENT_CONFIRMED("입금확인"),
    TICKET_ISSUED("발권완료"),
    ORDER_CANCELLED("취소완료");

    private final String displayName;

    SmsTemplateCode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
