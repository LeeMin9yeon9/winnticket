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

    // 서비스단에서 채워 넣는 생성될 배너 ID - INSERT 시 DB에서 생성하는 대신 미리 만들어서
    // 넘겨야, 같은 요청 안에서 배너-채널 연결(banner_channels)까지 바로 저장할 수 있음
    @Schema(hidden = true)
    private String id;

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
