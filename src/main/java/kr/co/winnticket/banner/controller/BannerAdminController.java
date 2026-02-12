package kr.co.winnticket.banner.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.banner.dto.BannerCreateDto;
import kr.co.winnticket.banner.dto.BannerDto;
import kr.co.winnticket.banner.dto.BannerFilter;
import kr.co.winnticket.banner.dto.BannerUpdateDto;
import kr.co.winnticket.banner.service.BannerService;
import kr.co.winnticket.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "관리자 배너", description = "배너 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/banners")
public class BannerAdminController {

    private final BannerService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "배너 생성")
    public ResponseEntity<ApiResponse<Void>> create(
            @RequestBody BannerCreateDto dto
    ) {
        service.create(dto);
        return ResponseEntity.ok(ApiResponse.success("생성 완료", null));
    }


    @PutMapping("/{id}")
    @Operation(summary = "배너 수정")
    public ResponseEntity<ApiResponse<Void>> update(
            @Parameter(description = "배너 ID")
            @PathVariable String id,
            @RequestBody BannerUpdateDto dto
    ) {
        service.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success("수정 완료", null));
    }


    @PatchMapping("/{id}/visible")
    @Operation(summary = "배너 활성/비활성", description = "visible=true/false 전환")
    public ResponseEntity<ApiResponse<Void>> changeVisible(
            @PathVariable String id,
            @RequestParam Boolean visible
    ) {
        service.changeVisible(id, visible);
        return ResponseEntity.ok(ApiResponse.success("상태 변경 완료", null));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "배너 삭제(소프트 삭제)", description = "visible=false 처리")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String id
    ) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("삭제 완료", null));
    }

    @GetMapping
    @Operation(summary = "배너 목록 조회 (검색/필터)")
    public ResponseEntity<ApiResponse<List<BannerDto>>> list(
            BannerFilter filter
    ) {
        return ResponseEntity.ok(ApiResponse.success("조회 성공", service.getAdminList(filter)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "배너 상세(관리자)")
    public ResponseEntity<ApiResponse<BannerDto>> detail(@PathVariable String id) {
        BannerDto dto = service.getAdminDetail(id);
        return ResponseEntity.ok(ApiResponse.success("조회 성공", dto));
    }


    @GetMapping("/{id}/click-count")
    @Operation(summary = "배너 총 클릭 수 조회")
    public ResponseEntity<ApiResponse<Long>> clickCount(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("조회 성공", service.getClickCount(id)));
    }


}