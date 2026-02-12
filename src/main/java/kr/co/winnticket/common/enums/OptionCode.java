package kr.co.winnticket.common.enums;

public enum OptionCode {
    MOBILE("모바일자동"),
    MOBILE_MANUAL("모바일수동"),
    OPTION("선택옵션");

    private final String displayName;

    OptionCode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
