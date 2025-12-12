package kr.co.winnticket.banner.dto;

import kr.co.winnticket.banner.enums.BannerPosition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerFilter {

    private String keyword;            // 이름/설명 검색
    private BannerPosition position;   // 위치 필터
    private Boolean visible;           // 노출 여부
}