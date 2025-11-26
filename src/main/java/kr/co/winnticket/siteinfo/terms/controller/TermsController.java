package kr.co.winnticket.siteinfo.terms.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.siteinfo.terms.dto.TermsRequest;
import kr.co.winnticket.siteinfo.terms.dto.TermsResponse;
import kr.co.winnticket.siteinfo.terms.service.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "약관정보", description = "약관 관리")
@RestController
@RequestMapping("/api/admin/terms")
@RequiredArgsConstructor
public class TermsController {

    private final TermsService termsService;

    // 전체 조회 (관리자용)
    @GetMapping
    public ApiResponse<List<TermsResponse>> getAllTerms() {
        List<TermsResponse> terms = termsService.getAllTerms();
        return ApiResponse.success("약관 목록 조회 성공", terms);
    }

    // 노출 약관 (공개용)
    @GetMapping("/visible")
    public ApiResponse<List<TermsResponse>> getVisibleTerms() {
        List<TermsResponse> terms = termsService.getVisibleTerms();
        return ApiResponse.success("노출 약관 목록 조회 성공", terms);
    }

    // 필수 약관 (회원가입용)
    @GetMapping("/required")
    public ApiResponse<List<TermsResponse>> getRequiredTerms() {
        List<TermsResponse> terms = termsService.getRequiredTerms();
        return ApiResponse.success("필수 약관 목록 조회 성공", terms);
    }

    // 단일 조회
    @GetMapping("/{id}")
    public ApiResponse<TermsResponse> getTerms(@PathVariable Long id) {
        TermsResponse terms = termsService.getTerms(id);
        return ApiResponse.success("약관 조회 성공", terms);
    }

    // 등록
    @PostMapping
    public ApiResponse<TermsResponse> createTerms(@RequestBody TermsRequest request) {
        String username = "system";
        TermsResponse terms = termsService.createTerms(request, username);
        return ApiResponse.success("약관 등록 성공", terms);
    }

    // 수정
    @PutMapping("/{id}")
    public ApiResponse<TermsResponse> updateTerms(
            @PathVariable Long id,
            @RequestBody TermsRequest request
    ) {
        String username = "system";
        TermsResponse terms = termsService.updateTerms(id, request, username);
        return ApiResponse.success("약관 수정 성공", terms);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTerms(@PathVariable Long id) {
        termsService.deleteTerms(id);
        return ApiResponse.success("약관 삭제 성공", null);
    }
}