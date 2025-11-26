package kr.co.winnticket.siteinfo.companyinfo.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteInfoRequest {
    // 기본정보
    private String companyName;
    private String businessNumber;
    private String ceoName;
    private LocalDate establishedDate;

    // 연락처정보
    private String address;
    private String addressDetail;
    private String postalCode;
    private String tel;
    private String fax;
    private String email;

    // 고객센터 정보
    private String customerServiceTel;
    private String customerServiceEmail;
    private String businessHours;

    // 법적 정보
    private String onlineMarketingNumber;
    private String privacyOfficerName;
    private String privacyOfficerEmail;

    // 추가정보
    private String companyIntroduction;
    private String termsOfService;
    private String privacyPolicy;
    private String refundPolicy;
}
