package kr.co.winnticket.popup.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "popup_pages")
public class PopupPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 페이지 path 패턴
     * 예: /, /product/*, /event/*
     */
    @Column(nullable = false, length = 200)
    private String pathPattern;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id", nullable = false)
    private Popup popup;
}