package kr.co.winnticket.siteinfo.terms.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.siteinfo.terms.dto.TermsReqDto;
import kr.co.winnticket.siteinfo.terms.dto.TermsResDto;
import kr.co.winnticket.siteinfo.terms.service.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "약관정보", description = "약관 관리")
@RestController
@RequestMapping("/api/admin/terms")
@RequiredArgsConstructor
public class TermsController {

    private final TermsService service;

    @Operation(summary = "전체 조회")
    @GetMapping
    public ApiResponse<List<TermsResDto>> getAllTerms() {
        return ApiResponse.success("조회 성공", service.getAllTerms());
    }

    @Operation(summary = "노출 약관")
    @GetMapping("/visible")
    public ApiResponse<List<TermsResDto>> getVisibleTerms() {
        return ApiResponse.success("조회 성공", service.getVisibleTerms());
    }

    @Operation(summary = "필수 약관")
    @GetMapping("/required")
    public ApiResponse<List<TermsResDto>> getRequiredTerms() {
        return ApiResponse.success("조회 성공", service.getRequiredTerms());
    }

    @Operation(summary = "단건 조회")
    @GetMapping("/{id}")
    public ApiResponse<TermsResDto> getTerms(@PathVariable Long id) {
        return ApiResponse.success("조회 성공", service.getTerms(id));
    }

    @Operation(summary = "등록")
    @PostMapping
    public ApiResponse<?> create(@RequestBody TermsReqDto req) {
        return ApiResponse.success("등록 성공",
                service.createTerms(req, "system"));
    }

    @Operation(summary = "수정")
    @PutMapping("/{id}")
    public ApiResponse<?> update(@PathVariable Long id,
                                 @RequestBody TermsReqDto req) {
        return ApiResponse.success("수정 성공",
                service.updateTerms(id, req, "system"));
    }

    @Operation(summary = "삭제")
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        service.deleteTerms(id);
        return ApiResponse.success("삭제 성공", null);
    }
}