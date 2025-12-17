package kr.co.winnticket.product.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.product.shop.dto.ProductShopDetailGetResDto;
import kr.co.winnticket.product.shop.dto.ProductShopListGetResDto;
import kr.co.winnticket.product.shop.dto.ShopMainResDto;
import kr.co.winnticket.product.shop.service.ProductShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductShopController {
    private final ProductShopService service;

    // 상품 목록 검색
    @GetMapping("api/product/shop/search")
    @Tag(name = "상품_쇼핑몰", description = "상품 관리")
    @Operation(summary = "상품 목록 검색", description = "전달받은 코드의 상품을 검색합니다.")
    public ResponseEntity<ApiResponse<List<ProductShopListGetResDto>>>  getProductListSearch (
            @Parameter(description = "상품명") @RequestParam(value = "name", required = false) String name
            ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectProductListSearch(name))
        );
    }

    // 상품 목록조회
    @GetMapping({"api/product/shop",
                "api/product/shop/{mainCategory}",
                "api/product/shop/{mainCategory}/{subCategory}"})
    @Tag(name = "상품_쇼핑몰", description = "상품 관리")
    @Operation(summary = "상품 목록 조회", description = "카테고리별 상품 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<ShopMainResDto>> getProductList (
            @Parameter(description = "카테고리코드1") @PathVariable(required = false) String mainCategory,
            @Parameter(description = "카테고리코드2") @PathVariable(required = false) String subCategory
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectProductList(mainCategory, subCategory))
        );
    }

    // 상품 상세조회
    @GetMapping("api/product/shop/detail/{code}")
    @Tag(name = "상품_쇼핑몰", description = "상품 관리")
    @Operation(summary = "상품 상세 조회", description = "전달받은 코드의 상품을 조회합니다.")
    public ResponseEntity<ApiResponse<ProductShopDetailGetResDto>> getProductDetail (
            @Parameter(description = "상품코드") @PathVariable("code") String code
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectProductDetail(code))
        );
    }

}
