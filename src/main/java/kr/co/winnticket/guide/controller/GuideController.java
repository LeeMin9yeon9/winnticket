package kr.co.winnticket.guide.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.guide.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "가이드", description = "관리자 메뉴별 투어 가이드 상태 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/guide")
public class GuideController {

    private final GuideService service;

    @GetMapping
    @Operation(summary = "가이드 상태 전체 조회", description = "현재 관리자의 모든 메뉴 가이드 열람 여부를 반환합니다.")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> getAll(Authentication auth) {
        String accountId = auth.getName();
        return ResponseEntity.ok(ApiResponse.success("조회 성공", service.getAll(accountId)));
    }

    @PutMapping("/{menuKey}")
    @Operation(summary = "가이드 상태 변경", description = "특정 메뉴의 가이드 열람 여부를 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> updateSeen(
            @PathVariable String menuKey,
            @RequestParam boolean seen,
            Authentication auth) {
        service.updateSeen(auth.getName(), menuKey, seen);
        return ResponseEntity.ok(ApiResponse.success("변경 완료", null));
    }
}
