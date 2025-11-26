package kr.co.winnticket.siteinfo.companyinfo.service;

import kr.co.winnticket.siteinfo.companyinfo.dto.CompanyIntroResponse;
import kr.co.winnticket.siteinfo.companyinfo.dto.SiteInfoRequest;
import kr.co.winnticket.siteinfo.companyinfo.dto.SiteInfoResponse;
import kr.co.winnticket.siteinfo.companyinfo.entity.SiteInfo;
import kr.co.winnticket.siteinfo.companyinfo.repository.SiteInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteInfoService {

    private final SiteInfoRepository siteInfoRepository;

    // 사이트 정보 조회
    public SiteInfoResponse getSiteInfo() {
        SiteInfo siteInfo = siteInfoRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("사이트 정보가 존재하지 않습니다."));
        return convertToResponse(siteInfo);
    }

    // 회사소개 조회
    public CompanyIntroResponse getCompanyIntro() {
        SiteInfo siteInfo = siteInfoRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("사이트 정보가 존재하지 않습니다."));

        return CompanyIntroResponse.builder()
                .companyName(siteInfo.getCompanyName())
                .businessNumber(siteInfo.getBusinessNumber())
                .ceoName(siteInfo.getCeoName())
                .establishedDate(siteInfo.getEstablishedDate())
                .address(siteInfo.getAddress())
                .tel(siteInfo.getTel())
                .email(siteInfo.getEmail())
                .companyIntroduction(siteInfo.getCompanyIntroduction())
                .build();
    }

    // 사이트 정보 등록 (최초 1회)
    @Transactional
    public SiteInfoResponse createSiteInfo(SiteInfoRequest request, String username) {
        if (siteInfoRepository.count() > 0) {
            throw new RuntimeException("사이트 정보가 이미 존재합니다. 수정을 이용해주세요.");
        }

        SiteInfo siteInfo = SiteInfo.builder()
                .companyName(request.getCompanyName())
                .businessNumber(request.getBusinessNumber())
                .ceoName(request.getCeoName())
                .establishedDate(request.getEstablishedDate())
                .address(request.getAddress())
                .addressDetail(request.getAddressDetail())
                .postalCode(request.getPostalCode())
                .tel(request.getTel())
                .fax(request.getFax())
                .email(request.getEmail())
                .customerServiceTel(request.getCustomerServiceTel())
                .customerServiceEmail(request.getCustomerServiceEmail())
                .businessHours(request.getBusinessHours())
                .onlineMarketingNumber(request.getOnlineMarketingNumber())
                .privacyOfficerName(request.getPrivacyOfficerName())
                .privacyOfficerEmail(request.getPrivacyOfficerEmail())
                .companyIntroduction(request.getCompanyIntroduction())
                .termsOfService(request.getTermsOfService())
                .privacyPolicy(request.getPrivacyPolicy())
                .refundPolicy(request.getRefundPolicy())
                .createdBy(username)
                .updatedBy(username)
                .build();

        SiteInfo saved = siteInfoRepository.save(siteInfo);
        return convertToResponse(saved);
    }

    // 사이트 정보 수정
    @Transactional
    public SiteInfoResponse updateSiteInfo(SiteInfoRequest request, String username) {
        SiteInfo siteInfo = siteInfoRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("사이트 정보가 존재하지 않습니다."));

        siteInfo.setCompanyName(request.getCompanyName());
        siteInfo.setBusinessNumber(request.getBusinessNumber());
        siteInfo.setCeoName(request.getCeoName());
        siteInfo.setEstablishedDate(request.getEstablishedDate());
        siteInfo.setAddress(request.getAddress());
        siteInfo.setAddressDetail(request.getAddressDetail());
        siteInfo.setPostalCode(request.getPostalCode());
        siteInfo.setTel(request.getTel());
        siteInfo.setFax(request.getFax());
        siteInfo.setEmail(request.getEmail());
        siteInfo.setCustomerServiceTel(request.getCustomerServiceTel());
        siteInfo.setCustomerServiceEmail(request.getCustomerServiceEmail());
        siteInfo.setBusinessHours(request.getBusinessHours());
        siteInfo.setOnlineMarketingNumber(request.getOnlineMarketingNumber());
        siteInfo.setPrivacyOfficerName(request.getPrivacyOfficerName());
        siteInfo.setPrivacyOfficerEmail(request.getPrivacyOfficerEmail());
        siteInfo.setCompanyIntroduction(request.getCompanyIntroduction());
        siteInfo.setTermsOfService(request.getTermsOfService());
        siteInfo.setPrivacyPolicy(request.getPrivacyPolicy());
        siteInfo.setRefundPolicy(request.getRefundPolicy());
        siteInfo.setUpdatedBy(username);

        return convertToResponse(siteInfo);
    }

    // Entity -> Response DTO
    private SiteInfoResponse convertToResponse(SiteInfo siteInfo) {
        return SiteInfoResponse.builder()
                .id(siteInfo.getId())
                .companyName(siteInfo.getCompanyName())
                .businessNumber(siteInfo.getBusinessNumber())
                .ceoName(siteInfo.getCeoName())
                .establishedDate(siteInfo.getEstablishedDate())
                .address(siteInfo.getAddress())
                .addressDetail(siteInfo.getAddressDetail())
                .postalCode(siteInfo.getPostalCode())
                .tel(siteInfo.getTel())
                .fax(siteInfo.getFax())
                .email(siteInfo.getEmail())
                .customerServiceTel(siteInfo.getCustomerServiceTel())
                .customerServiceEmail(siteInfo.getCustomerServiceEmail())
                .businessHours(siteInfo.getBusinessHours())
                .onlineMarketingNumber(siteInfo.getOnlineMarketingNumber())
                .privacyOfficerName(siteInfo.getPrivacyOfficerName())
                .privacyOfficerEmail(siteInfo.getPrivacyOfficerEmail())
                .companyIntroduction(siteInfo.getCompanyIntroduction())
                .termsOfService(siteInfo.getTermsOfService())
                .privacyPolicy(siteInfo.getPrivacyPolicy())
                .refundPolicy(siteInfo.getRefundPolicy())
                .createdAt(siteInfo.getCreatedAt())
                .updatedAt(siteInfo.getUpdatedAt())
                .createdBy(siteInfo.getCreatedBy())
                .updatedBy(siteInfo.getUpdatedBy())
                .build();
    }
}
