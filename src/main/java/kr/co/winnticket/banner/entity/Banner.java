package kr.co.winnticket.banner.entity;


import kr.co.winnticket.banner.enums.BannerClickAction;
import kr.co.winnticket.banner.enums.BannerPosition;
import kr.co.winnticket.banner.enums.BannerStatus;
import kr.co.winnticket.banner.enums.BannerType;
import kr.co.winnticket.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "banners")
public class Banner extends BaseEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BannerType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private BannerPosition position;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String imageUrlMobile;

    @Column(columnDefinition = "TEXT")
    private String htmlContent;

    @Column(length = 500)
    private String videoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BannerClickAction clickAction;

    @Column(length = 500)
    private String linkUrl;

    @Column(length = 10)
    private String linkTarget;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Boolean visible = true;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false)
    private Long clickCount = 0L;

    private Integer width;
    private Integer height;
    private Integer mobileWidth;
    private Integer mobileHeight;

    @OneToMany(mappedBy = "banner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BannerChannel> channels = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = "BANNER_" + UUID.randomUUID();
        }
    }

    public BannerStatus getStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (Boolean.FALSE.equals(visible)) return BannerStatus.INACTIVE;
        if (now.isBefore(startDate)) return BannerStatus.SCHEDULED;
        if (now.isAfter(endDate)) return BannerStatus.EXPIRED ;
        return BannerStatus.ACTIVE;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementClickCount() {
        this.clickCount++;
    }
}