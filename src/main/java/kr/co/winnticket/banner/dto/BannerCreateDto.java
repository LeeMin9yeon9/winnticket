package kr.co.winnticket.banner.dto;



import kr.co.winnticket.banner.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerCreateDto {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BannerType type;

    @NotNull
    private BannerPosition position;

    private String imageUrl;
    private String imageUrlMobile;
    private String htmlContent;
    private String videoUrl;

    @NotNull
    private BannerClickAction clickAction;

    private String linkUrl;
    private String linkTarget;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    private Boolean visible = true;
    private Integer displayOrder = 0;

    private Integer width;
    private Integer height;
    private Integer mobileWidth;
    private Integer mobileHeight;

    private List<UUID> channelIds;
}
