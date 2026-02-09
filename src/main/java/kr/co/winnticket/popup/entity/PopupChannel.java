package kr.co.winnticket.popup.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "popup_channels")
public class PopupChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 예: 몰 아이디, 도메인 아이디, 채널 코드 등
    @Column(nullable = false, length = 50)
    private UUID channelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popup;
}