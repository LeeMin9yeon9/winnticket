package kr.co.winnticket.banner.enums;

public enum BannerStatus {
    INACTIVE,   // 비활성
    SCHEDULED,  // 예정 (startDate 전)
    ACTIVE,     // 활성
    EXPIRED     // 만료 (endDate 지남)
}