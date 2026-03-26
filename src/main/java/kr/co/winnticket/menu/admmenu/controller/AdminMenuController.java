package kr.co.winnticket.menu.admmenu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuCreateDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuListDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuUpdateDto;
import kr.co.winnticket.menu.admmenu.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "관리자 메뉴 관리", description = "관리자 메뉴 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/menu")
public class AdminMenuController {

    private final AdminMenuService service;

    @GetMapping
    @Operation(summary = "관리자 메뉴 조회")
    public ApiResponse<List<AdminMenuListDto>> getMenus(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String titleEn,
            @RequestParam(required = false) String page
    ) {
        return ApiResponse.success("조회 성공",
                service.searchAdminMenus(title, titleEn, page));
    }

    @PostMapping
    @Operation(summary = "관리자 메뉴 생성")
    public ApiResponse<Void> create(@RequestBody AdminMenuCreateDto dto) {
        service.createAdmMenu(dto);
        return ApiResponse.success("생성 완료", null);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "관리자 메뉴 수정")
    public ApiResponse<Void> update(
            @PathVariable UUID id,
            @RequestBody AdminMenuUpdateDto dto
    ) {
        service.updateAdmMenu(id, dto);
        return ApiResponse.success("수정 완료", null);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "관리자 메뉴 삭제")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        service.deleteAdmMenu(id);
        return ApiResponse.success("삭제 완료", null);
    }

    @PatchMapping("/{id}/order")
    @Operation(summary = "메뉴 순서 변경")
    public ApiResponse<Void> changeOrder(
            @PathVariable UUID id,
            @RequestBody java.util.Map<String, Integer> body
    ) {
        Integer newOrder = body.get("displayOrder");
        if (newOrder == null) newOrder = body.get("newOrder");
        if (newOrder == null) {
            return ApiResponse.error("displayOrder 또는 newOrder는 필수입니다.", "MISSING_PARAM");
        }
        service.changeAdmMenu(id, newOrder);
        return ApiResponse.success("순서 변경 완료", null);
    }

    @PatchMapping("/{id}/visible")
    @Operation(summary = "노출 여부 변경")
    public ApiResponse<Void> changeVisible(
            @PathVariable UUID id,
            @RequestParam Boolean visible
    ) {
        service.changeVisible(id, visible);
        return ApiResponse.success("노출 변경 완료", null);
    }
}
