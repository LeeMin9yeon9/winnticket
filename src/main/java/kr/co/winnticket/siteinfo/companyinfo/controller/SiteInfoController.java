package kr.co.winnticket.siteinfo.companyinfo.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.siteinfo.companyinfo.dto.CompanyIntroResponse;
import kr.co.winnticket.siteinfo.companyinfo.dto.SiteInfoRequest;
import kr.co.winnticket.siteinfo.companyinfo.dto.SiteInfoResponse;
import kr.co.winnticket.siteinfo.companyinfo.service.SiteInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회사정보", description = "회사 소개")
@RestController
@RequestMapping("/api/admin/site-info")
@RequiredArgsConstructor
public class SiteInfoController {

    private final SiteInfoService siteInfoService;

    // 사이트 정보 조회 (공개)
    @GetMapping
    public ApiResponse<SiteInfoResponse> getSiteInfo() {
        SiteInfoResponse response = siteInfoService.getSiteInfo();
        return ApiResponse.success("사이트 정보 조회 성공", response);
    }

    // 회사소개 조회 (공개)
    @GetMapping("/company-intro")
    public ApiResponse<CompanyIntroResponse> getCompanyIntro() {
        CompanyIntroResponse response = siteInfoService.getCompanyIntro();
        return ApiResponse.success("회사소개 조회 성공", response);
    }

    // 사이트 정보 등록 (지금은 누구나 호출 가능, 나중에 Security 붙이면 ADMIN만)
    @PostMapping
    public ApiResponse<SiteInfoResponse> createSiteInfo(@RequestBody SiteInfoRequest request) {
        String username = "system"; // 나중에 로그인 붙이면 실제 사용자 ID로 변경
        SiteInfoResponse response = siteInfoService.createSiteInfo(request, username);
        return ApiResponse.success("사이트 정보 등록 성공", response);
    }

    // 사이트 정보 수정
    @PutMapping
    public ApiResponse<SiteInfoResponse> updateSiteInfo(@RequestBody SiteInfoRequest request) {
        String username = "system";
        SiteInfoResponse response = siteInfoService.updateSiteInfo(request, username);
        return ApiResponse.success("사이트 정보 수정 성공", response);
    }
}
