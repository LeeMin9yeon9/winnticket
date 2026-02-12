package kr.co.winnticket.banner.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.winnticket.banner.enums.BannerClickAction;
import kr.co.winnticket.banner.enums.BannerPosition;
import kr.co.winnticket.banner.enums.BannerStatus;
import kr.co.winnticket.banner.enums.BannerType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(title = "[ADMIN > 배너 정보 DTO] BannerDto")
public class BannerDto {

    @Schema(description = "배너 ID")
    private String id;

    @Schema(description = "배너명")
    private String name;

    @Schema(description = "배너 설명")
    private String description;

    @Schema(description = "배너 타입")
    private BannerType type;

    @Schema(description = "배너 노출 위치")
    private BannerPosition position;

    @Schema(description = "이미지URL")
    private String imageUrl;

    @Schema(description = "모바일이미지URL")
    private String imageUrlMobile;

    @Schema(description = "HTML 직접 입력 콘텐츠")
    private String htmlContent;

    @Schema(description = "동영상 URL")
    private String videoUrl;

    @Schema(description = "클릭 액션 타입")
    private BannerClickAction clickAction;

    @Schema(description = "링크 URL")
    private String linkUrl;

    @Schema(description = "링크 타켓")
    private String linkTarget;

    @Schema(description = "노출 시작일시")
    private LocalDateTime startDate;

    @Schema(description = "노출 종료일시")
    private LocalDateTime endDate;

    @Schema(description = "노출 여부", example = "true")
    private Boolean visible;

    @Schema(description = "정렬 순서", example = "1")
    private Integer displayOrder;

    @Schema(description = "조회수")
    private Long viewCount;

    @Schema(description = "클릭수")
    private Long clickCount;

    @Schema(description = "PC 이미지 가로 사이즈")
    private Integer width;

    @Schema(description = "PC 이미지 세로 사이즈")
    private Integer height;

    @Schema(description = "모바일 이미지 가로 사이즈")
    private Integer mobileWidth;

    @Schema(description = "모바일 이미지 세로 사이즈")
    private Integer mobileHeight;

    @Schema(description = "연결된 채널 ID 목록")
    private List<String> channelIds;

    @Schema(description = "배너 상태")
    private BannerStatus status;

    @Schema(description = "생성일시", example = "2026-02-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2026-02-05T15:30:00")
    private LocalDateTime updatedAt;
}