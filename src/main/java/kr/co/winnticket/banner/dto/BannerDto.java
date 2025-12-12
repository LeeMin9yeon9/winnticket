package kr.co.winnticket.banner.dto;


import kr.co.winnticket.banner.enums.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerDto {

    private String id;
    private String name;
    private String description;
    private BannerType type;
    private BannerPosition position;
    private String imageUrl;
    private String imageUrlMobile;
    private String htmlContent;
    private String videoUrl;
    private BannerClickAction clickAction;
    private String linkUrl;
    private String linkTarget;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean visible;
    private Integer displayOrder;
    private Long viewCount;
    private Long clickCount;
    private Integer width;
    private Integer height;
    private Integer mobileWidth;
    private Integer mobileHeight;
    private List<UUID> channelIds;
    private BannerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}