package kr.co.winnticket.banner.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "배너 클릭 동작 타입")
public enum BannerClickAction {
    LINK("링크 이동"),
    POPUP("팝업 노출"),
    NONE("동작 없음");

    private final String clickAction;

    BannerClickAction(String clickAction) {
        this.clickAction = clickAction;
    }

    public String getClickAction() {
        return clickAction;
    }
}