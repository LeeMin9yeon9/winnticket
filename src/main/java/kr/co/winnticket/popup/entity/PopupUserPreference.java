package kr.co.winnticket.popup.entity;


import kr.co.winnticket.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "popup_user_preferences",
        indexes = {
                @Index(name = "idx_popup_user", columnList = "popupId, userId"),
                @Index(name = "idx_popup_session", columnList = "popupId, sessionId")
        }
)
public class PopupUserPreference extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String popupId;

    @Column(length = 50)
    private String userId;      // 로그인 사용자

    @Column(length = 100)
    private String sessionId;   // 비로그인 사용자의 세션ID 등

    // "다시 보지 않기"
    @Column(nullable = false)
    private Boolean neverShow = false;

    // "오늘 하루 보지 않기" 등 유효기간
    private LocalDateTime closedUntil;
}