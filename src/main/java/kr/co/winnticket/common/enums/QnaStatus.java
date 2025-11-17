package kr.co.winnticket.common.enums;

public enum QnaStatus {
    PENDING("대기중"),
    ANSWERED("답변완료");

    private final String displayName;

    QnaStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
