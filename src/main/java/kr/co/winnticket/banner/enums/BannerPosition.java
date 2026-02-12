package kr.co.winnticket.banner.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "배너 노출 위")
public enum BannerPosition {
    MAIN_TOP("메인 상단"),
    MAIN_MIDDLE("메인 중단"),
    MAIN_BOTTOM("메인 하단");

    private final String position;

    BannerPosition(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }
}
