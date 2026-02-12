package kr.co.winnticket.banner.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.banner.enums.BannerClickAction;
import kr.co.winnticket.banner.enums.BannerPosition;
import kr.co.winnticket.banner.enums.BannerType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(title = "[ADMIN > 배너 생성 DTO] BannerCreateDto")
public class BannerCreateDto {

    @NotBlank
    @Schema(description = "배너명")
    private String name;

    @Schema(description = "배너설명")
    private String description;

    @NotNull
    @Schema(description = "배너 타입")
    private BannerType type;

    @NotNull
    @Schema(description = "노출 위치")
    private BannerPosition position;

    @Schema(description = "PC 이미지 URL")
    private String imageUrl;

    @Schema(description = "모바일 이미지 URL")
    private String imageUrlMobile;

    @Schema(description = "HTML 콘텐츠")
    private String htmlContent;

    @Schema(description = "동영상 URL")
    private String videoUrl;

    @NotNull
    @Schema(description = "클릭 애션")
    private BannerClickAction clickAction;

    @Schema(description = "이동 링크")
    private String linkUrl;

    @Schema(description = "이동 타켓")
    private String linkTarget;

    @NotNull
    @Schema(description = "노출 시작일")
    private LocalDateTime startDate;

    @NotNull
    @Schema(description = "노출 종료일")
    private LocalDateTime endDate;

    @Schema(description = "배너 활/비활성화")
    private Boolean visible = true;

    @Schema(description = "노출순서")
    private Integer displayOrder = 0;

    private Integer width;
    private Integer height;
    private Integer mobileWidth;
    private Integer mobileHeight;

    private List<String> channelIds;
}
