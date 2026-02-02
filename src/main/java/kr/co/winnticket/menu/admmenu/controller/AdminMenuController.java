package kr.co.winnticket.menu.admmenu.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuCreateDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuListDto;
import kr.co.winnticket.menu.admmenu.dto.AdminMenuUpdateDto;
import kr.co.winnticket.menu.admmenu.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "관리자 메뉴 관리", description = "관리자 메뉴 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/menu")
public class AdminMenuController {

    private final AdminMenuService adminMenuService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminMenuListDto>>> getAllAdmMenus(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String titleEn,
            @RequestParam(required = false) String page
    ) {
        List<AdminMenuListDto> menus = adminMenuService.searchAdminMenus(title, titleEn, page);
        return ResponseEntity.ok(ApiResponse.success(menus));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createAdmMenu(
            @RequestBody AdminMenuCreateDto createDto
    ) {
        adminMenuService.createAdmMenu(createDto);
        return ResponseEntity.ok(ApiResponse.success("관리자 메뉴가 생성되었습니다.", null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateAdmMenu(
            @PathVariable UUID id,
            @RequestBody AdminMenuUpdateDto updateDto
    ) throws NotFoundException {
        adminMenuService.updateAdmMenu(id, updateDto);
        return ResponseEntity.ok(ApiResponse.success("관리자 메뉴가 수정되었습니다.", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAdmMenu(
            @PathVariable UUID id
    ) throws NotFoundException {
        adminMenuService.deleteAdmMenu(id);
        return ResponseEntity.ok(ApiResponse.success("관리자 메뉴가 삭제되었습니다.", null));
    }

    @PatchMapping("/displayOrder/{id}/{newOrder}")
    public ResponseEntity<ApiResponse<Void>> changeAdmOrder(
            @PathVariable UUID id,
            @PathVariable Integer newOrder
    ) throws NotFoundException {
        adminMenuService.changeAdmMenu(id, newOrder);
        return ResponseEntity.ok(ApiResponse.success("순서가 변경되었습니다.", null));
    }

    @PatchMapping("/visible/{id}/{visible}")
    public ResponseEntity<ApiResponse<Void>> changeAdmVisible(
            @PathVariable UUID id,
            @PathVariable Boolean visible
    ) {
        adminMenuService.changeVisible(id, visible);
        return ResponseEntity.ok(ApiResponse.success("노출 상태가 변경되었습니다.", null));
    }
}
