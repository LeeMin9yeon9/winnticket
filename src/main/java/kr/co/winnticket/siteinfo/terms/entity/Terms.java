package kr.co.winnticket.siteinfo.terms.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "terms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Terms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    @Comment("약관 제목")
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @Comment("약관 내용 (HTML)")
    private String content;

    @Column(name = "required", nullable = false)
    @Comment("필수 약관 여부")
    private Boolean required = true;

    @Column(name = "display_order", nullable = false)
    @Comment("표시 순서")
    private Integer displayOrder = 0;

    @Column(name = "visible", nullable = false)
    @Comment("노출 여부")
    private Boolean visible = true;

    // 메타정보
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
