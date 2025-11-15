package kr.co.winnticket.community.notice.enums;

public enum PostType {
    NOTICE("공지사항"),
    FAQ("FAQ"),
    QNA("QNA"),
    EVENT("이벤트");

    private final String displayName;

    PostType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
