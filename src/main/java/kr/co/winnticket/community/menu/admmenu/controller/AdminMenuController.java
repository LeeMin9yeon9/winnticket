package kr.co.winnticket.community.menu.admmenu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.community.menu.admmenu.dto.AdminMenuCreateDto;
import kr.co.winnticket.community.menu.admmenu.dto.AdminMenuListDto;
import kr.co.winnticket.community.menu.admmenu.dto.AdminMenuUpdateDto;
import kr.co.winnticket.community.menu.admmenu.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "관리자 메뉴 관리", description = "관리자 메뉴 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu/adminMenus")
public class AdminMenuController {

    private final AdminMenuService adminMenuService;

    // 관리자 메뉴 전체 리스트 조회 (검색 조건 포함)
    @GetMapping
    @Operation(summary = "관리자 메뉴 목록 조회", description = "관리자 메뉴 목록을 검색하여 조회합니다.")
    public ResponseEntity<List<AdminMenuListDto>> getAllAdmMenus(
           @Parameter(description = "메뉴명") @RequestParam(required = false) String title,
           @Parameter(description = "메뉴명_영문") @RequestParam(required = false) String titleEn,
           @Parameter(description = "페이지명") @RequestParam(required = false) String page
    ) {
        List<AdminMenuListDto> menus = adminMenuService.searchAdminMenus(title, titleEn, page);
        return ResponseEntity.ok(menus);
    }

    // 관리자 메뉴 생성
    @PostMapping
    @Operation(summary = "관리자 메뉴 생성", description = "새로운 관리자 메뉴를 생성합니다.")
    public ResponseEntity<String> createAdmMenu(@RequestBody AdminMenuCreateDto createDto) {
        adminMenuService.createAdmMenu(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Created");
    }

    // 관리자 메뉴 수정
    @PatchMapping("/{id}")
    @Operation(summary = "관리자 메뉴 수정", description = "관리자 메뉴 정보를 수정합니다.")
    public ResponseEntity<String> updateAdmMenu(
            @PathVariable UUID id,
            @RequestBody AdminMenuUpdateDto updateDto
    ) throws NotFoundException {
        boolean updated = adminMenuService.updateAdmMenu(id,updateDto);
        return updated ? ResponseEntity.ok("Updated") : ResponseEntity.notFound().build();
    }

    // 관리자 메뉴 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "관리자 메뉴 삭제", description = "관리자 메뉴를 삭제합니다.")
    public ResponseEntity<Void> deleteAdmMenu(@PathVariable UUID id) throws NotFoundException {
        adminMenuService.deleteAdmMenu(id);
        return ResponseEntity.noContent().build();
    }

    // 메뉴 노출 순서 변경
    @PatchMapping("/displayOrder/{id}/{newOrder}")
    @Operation(summary = "관리자 메뉴 순서 변경", description = "관리자 메뉴의 노출 순서를 변경합니다.")
    public ResponseEntity<String> changeAdmOrder(
            @PathVariable UUID id,
            @PathVariable Integer newOrder
    ) throws NotFoundException {
        adminMenuService.changeAdmMenu(id, newOrder);
        return ResponseEntity.ok("Order Updated");
    }

    // 메뉴 활성/비활성 변경
    @PatchMapping("visible/{id}/{visible}")
    @Operation(summary = "관리자 메뉴 활성/비활성 변경", description = "관리자 메뉴의 표시 상태를 변경합니다.")
    public ResponseEntity<String> changeAdmVisible(
            @PathVariable UUID id,
            @PathVariable Boolean visible
    ) throws NotFoundException {
        adminMenuService.changeVisible(id, visible);
        return ResponseEntity.ok("Visible Updated");
    }

    // 메뉴 순서 자동정렬

}
