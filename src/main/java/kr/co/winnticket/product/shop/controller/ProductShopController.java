package kr.co.winnticket.product.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.product.shop.dto.ProductShopListGetResDto;
import kr.co.winnticket.product.shop.service.ProductShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductShopController {
    private final ProductShopService service;

    // 상품 목록조회
    @GetMapping("api/product/shop")
    @Tag(name = "상품_쇼핑몰", description = "상품 관리")
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ProductShopListGetResDto>>> getProductList (
            @Parameter(description = "상품명") @RequestParam(value = "name", required = false) String name,
            @Parameter(description = "카테고리") @RequestParam(value = "categoryId", required = false) UUID categoryId
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectProductList(name, categoryId))
        );
    }

}
