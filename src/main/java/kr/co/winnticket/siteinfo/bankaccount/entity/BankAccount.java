package kr.co.winnticket.siteinfo.bankaccount.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "bank_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bank_name", nullable = false, length = 100)
    @Comment("은행명")
    private String bankName;

    @Column(name = "account_number", nullable = false, length = 100)
    @Comment("계좌번호")
    private String accountNumber;

    @Column(name = "account_holder", nullable = false, length = 100)
    @Comment("예금주")
    private String accountHolder;

    @Column(name = "visible", nullable = false)
    @Comment("노출 여부")
    private Boolean visible = true;

    @Column(name = "display_order", nullable = false)
    @Comment("표시 순서")
    private Integer displayOrder = 0;

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
