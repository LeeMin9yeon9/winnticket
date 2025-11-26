package kr.co.winnticket.siteinfo.companyinfo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "site_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SiteInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 기본정보
    @Column(name = "company_name", nullable = false, length = 255)
    @Comment("회사명")
    private String companyName;

    @Column(name = "business_number", length = 50)
    @Comment("사업자등록번호")
    private String businessNumber;

    @Column(name = "ceo_name", length = 100)
    @Comment("대표자명")
    private String ceoName;

    @Column(name = "established_date")
    @Comment("설립일")
    private LocalDate establishedDate;

    // 연락처정보
    @Column(name = "address", length = 500)
    @Comment("주소")
    private String address;

    @Column(name = "address_detail", length = 500)
    @Comment("상세주소")
    private String addressDetail;

    @Column(name = "postal_code", length = 20)
    @Comment("우편번호")
    private String postalCode;

    @Column(name = "tel", length = 50)
    @Comment("대표전화")
    private String tel;

    @Column(name = "fax", length = 50)
    @Comment("팩스번호")
    private String fax;

    @Column(name = "email", length = 255)
    @Comment("대표이메일")
    private String email;

    // 고객센터 정보
    @Column(name = "customer_service_tel", length = 50)
    @Comment("고객센터 전화번호")
    private String customerServiceTel;

    @Column(name = "customer_service_email", length = 255)
    @Comment("고객센터 이메일")
    private String customerServiceEmail;

    @Column(name = "business_hours", length = 255)
    @Comment("운영시간")
    private String businessHours;

    // 법적 정보
    @Column(name = "online_marketing_number", length = 100)
    @Comment("통신판매업신고번호")
    private String onlineMarketingNumber;

    @Column(name = "privacy_officer_name", length = 100)
    @Comment("개인정보보호책임자 이름")
    private String privacyOfficerName;

    @Column(name = "privacy_officer_email", length = 255)
    @Comment("개인정보보호책임자 이메일")
    private String privacyOfficerEmail;

    // 추가정보
    @Column(name = "company_introduction", columnDefinition = "TEXT")
    @Comment("회사소개")
    private String companyIntroduction;

    @Column(name = "terms_of_service", columnDefinition = "TEXT")
    @Comment("이용약관")
    private String termsOfService;

    @Column(name = "privacy_policy", columnDefinition = "TEXT")
    @Comment("개인정보처리방침")
    private String privacyPolicy;

    @Column(name = "refund_policy", columnDefinition = "TEXT")
    @Comment("환불정책")
    private String refundPolicy;

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
