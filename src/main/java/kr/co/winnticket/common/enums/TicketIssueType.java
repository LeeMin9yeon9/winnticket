package kr.co.winnticket.common.enums;

public enum TicketIssueType {
    PREPURCHASE("선사입"),
    EXTERNAL("외부연동"),
    AUTO("자동생성");

    private final String displayName;

    TicketIssueType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
