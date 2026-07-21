package kr.co.winnticket.common.enums;

public enum VerificationPurpose {
    POINT_EXCHANGE("포인트 이용권 전환");

    private final String displayName;

    VerificationPurpose(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
