package kr.co.winnticket.menu.menu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.menu.menu.dto.UpdateMenuDto;
import kr.co.winnticket.menu.menu.service.MenuCategoryService;
import kr.co.winnticket.menu.menu.dto.CreateMenuDto;
import kr.co.winnticket.menu.menu.dto.CreateSubMenuDto;
import kr.co.winnticket.menu.menu.dto.MenuListDto;
import kr.co.winnticket.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "쇼핑몰 메뉴 관리", description = "쇼핑몰 메뉴 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/menu/menuCategory")
public class MenuCategoryController {

    private final MenuCategoryService menuService;

    @GetMapping
    @Operation(summary = "쇼핑몰 메뉴 전체 조회")
    public ResponseEntity<ApiResponse<List<MenuListDto>>> getAllMenus(
            @Parameter(description = "메뉴이름")
            @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "메뉴코드")
            @RequestParam(value = "code", required = false) String code
    ){
        List<MenuListDto> list = menuService.menuList(name, code);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping
    @Operation(summary = "쇼핑몰 메뉴 생성")
    public ResponseEntity<ApiResponse<Void>> createMenu(
            @RequestBody CreateMenuDto createMenuDto
    ){
        menuService.createMenu(createMenuDto);
        return ResponseEntity.ok(ApiResponse.success("메뉴가 생성되었습니다.", null));
    }

    @PostMapping("/sub/{parentId}")
    @Operation(summary = "쇼핑몰 하위 메뉴 생성")
    public ResponseEntity<ApiResponse<Void>> createSubMenu(
            @PathVariable UUID parentId,
            @RequestBody CreateSubMenuDto createSubMenuDto
    ){
        menuService.createSubMenu(parentId, createSubMenuDto);
        return ResponseEntity.ok(ApiResponse.success("하위 메뉴가 생성되었습니다.", null));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "쇼핑몰 메뉴 수정")
    public ResponseEntity<ApiResponse<Void>> updateMenu(
            @PathVariable UUID id,
            @RequestBody UpdateMenuDto updateMenuDto
    ) throws NotFoundException {
        menuService.updateMenu(id, updateMenuDto);
        return ResponseEntity.ok(ApiResponse.success("메뉴가 수정되었습니다.", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "쇼핑몰 메뉴 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(
            @PathVariable UUID id
    ) throws NotFoundException {
        menuService.deleteMenu(id);
        return ResponseEntity.ok(ApiResponse.success("메뉴가 삭제되었습니다.", null));
    }

    @PatchMapping("/displayOrder/{id}/{displayOrder}")
    @Operation(summary = "쇼핑몰 메뉴 순서 변경")
    public ResponseEntity<ApiResponse<Void>> changeMenuOrder(
            @PathVariable UUID id,
            @PathVariable Integer displayOrder
    ){
        menuService.changeMenu(id, displayOrder);
        return ResponseEntity.ok(ApiResponse.success("순서가 변경되었습니다.", null));
    }

    @PatchMapping("/displayOrder/up/{id}")
    @Operation(summary = "쇼핑몰 메뉴 순서를 위로 이동")
    public ResponseEntity<ApiResponse<Void>> moveUp(
            @PathVariable UUID id
    ) throws NotFoundException {
        menuService.moveUp(id);
        return ResponseEntity.ok(ApiResponse.success("위로 이동되었습니다.", null));
    }

    @PatchMapping("/displayOrder/down/{id}")
    @Operation(summary = "쇼핑몰 메뉴 순서를 아래로 이동")
    public ResponseEntity<ApiResponse<Void>> moveDown(
            @PathVariable UUID id
    ) throws NotFoundException {
        menuService.moveDown(id);
        return ResponseEntity.ok(ApiResponse.success("아래로 이동되었습니다.", null));
    }

    @PatchMapping("/visible/{id}/{visible}")
    @Operation(summary = "쇼핑몰 메뉴 활성화/비활성화")
    public ResponseEntity<ApiResponse<Void>> visibleMenu(
            @PathVariable UUID id,
            @PathVariable Boolean visible
    ){
        menuService.changeVisible(id, visible);
        return ResponseEntity.ok(ApiResponse.success("메뉴 활성/비활성 처리되었습니다.", null));
    }

    @GetMapping("/shopMenus")
    @Operation(summary = "SHOP 쇼핑몰 메뉴 조회")
    public ResponseEntity<ApiResponse<List<MenuListDto>>> getShopMenus(){
        List<MenuListDto> list = menuService.getShopMenus();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
