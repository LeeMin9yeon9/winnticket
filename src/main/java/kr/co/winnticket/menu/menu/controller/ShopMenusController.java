package kr.co.winnticket.menu.menu.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.menu.menu.dto.MenuListDto;
import kr.co.winnticket.menu.menu.service.MenuCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "쇼핑몰 메뉴", description = "쇼핑몰 메뉴 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop")
public class ShopMenusController {

    private final MenuCategoryService menuService;
    @GetMapping("/menus")
    @Operation(summary = "SHOP 쇼핑몰 메뉴 조회")
    public ResponseEntity<ApiResponse<List<MenuListDto>>> getShopMenus(){
        List<MenuListDto> list = menuService.getShopMenus();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
