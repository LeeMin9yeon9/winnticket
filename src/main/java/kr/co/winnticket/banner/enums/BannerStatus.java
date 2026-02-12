package kr.co.winnticket.banner.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "배너 상태")
public enum BannerStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    SCHEDULED("노출 예정 (startDate 전)"),
    EXPIRED("노출 종료")
    ;

    private final String status;

    public String getStatus() {
        return status;
    }

    BannerStatus(String status) {
        this.status = status;
    }
}

