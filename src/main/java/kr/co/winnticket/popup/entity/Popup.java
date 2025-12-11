package kr.co.winnticket.popup.entity;


import  kr.co.winnticket.common.entity.BaseEntity;
import  kr.co.winnticket.popup.enums.PopupShowCondition;
import  kr.co.winnticket.popup.enums.PopupStatus;
import  kr.co.winnticket.popup.enums.PopupType;
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
@Table(name = "popups")
public class Popup extends BaseEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;      // 관리용 이름

    @Column(nullable = false, length = 200)
    private String title;     // 팝업 타이틀(사용자 노출)

    @Column(columnDefinition = "TEXT")
    private String contentHtml;   // 팝업 내용 (HTML)

    @Column(length = 500)
    private String imageUrl;      // 이미지 팝업인 경우

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PopupType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PopupShowCondition showCondition = PopupShowCondition.ALWAYS;

    // 노출 조건
    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Boolean visible = true;

    // 사이즈/위치
    private Integer width;
    private Integer height;

    private Integer positionTop;    // px
    private Integer positionLeft;   // px

    // 통계용
    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false)
    private Long clickCount = 0L;

    // 관계
    @OneToMany(mappedBy = "popup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PopupChannel> channels = new ArrayList<>();

    @OneToMany(mappedBy = "popup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PopupPage> pages = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = "POPUP_" + UUID.randomUUID();
        }
    }

    public PopupStatus getStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (Boolean.FALSE.equals(visible)) return PopupStatus.INACTIVE;
        if (now.isBefore(startDate)) return PopupStatus.SCHEDULED;
        if (now.isAfter(endDate)) return PopupStatus.EXPIRED;
        return PopupStatus.ACTIVE;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementClickCount() {
        this.clickCount++;
    }
}
