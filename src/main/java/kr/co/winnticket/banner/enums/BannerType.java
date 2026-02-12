package kr.co.winnticket.banner.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "배너 타입")
public enum BannerType {
    IMAGE ("이미지"),
    HTML ("HTML"),
    VIDEO("동영상");

    private final String type;

    BannerType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
}