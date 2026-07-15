package kr.co.winnticket.common.enums;

public enum AgeCategory {

    ADULT("성인"),
    YOUTH("청소년"),
    CHILD("아동");

    private final String displayName;

    AgeCategory(String displayName){this.displayName = displayName;}

    public String getDisplayName(){return displayName;}
}
