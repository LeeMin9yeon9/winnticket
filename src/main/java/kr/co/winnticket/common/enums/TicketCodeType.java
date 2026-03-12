package kr.co.winnticket.common.enums;

public enum TicketCodeType {
    NUMBER("쿠폰번호"),
    BARCODE("바코드"),
    QR("QR"),
    NONE("해당없음");

    private final String displayName;

    TicketCodeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    }

