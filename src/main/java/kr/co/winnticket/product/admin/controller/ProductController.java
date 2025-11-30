package kr.co.winnticket.product.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.winnticket.product.admin.dto.*;
import kr.co.winnticket.product.admin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "상품_관리자", description = "상품 관리")
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    // 상품 목록조회
    @GetMapping("api/product/admin")
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    public List<ProductListGetResDto> getProductList (
            @Parameter(description = "검색어") @RequestParam(value = "srchWord", required = false) String srchWord,
            @Parameter(description = "카테고리") @RequestParam(value = "categoryId", required = false) UUID categoryId,
            @Parameter(description = "상태 [ALL:전체, READY:준비중, ON_SALE:판매중, SOLD_OUT:품절, STOPPED:판매중단]") @RequestParam(value = "salesStatus", required = false) String salesStatus
    ) throws Exception {
        return service.selectProductList(srchWord, categoryId, salesStatus);
    }

    // 상품 기본정보 조회
    @GetMapping("api/product/admin/{id}")
    @Operation(summary = "상품 기본정보 조회", description = "전달받은 id의 상품의 상세정보를 조회합니다.")
    public ProductDetailGetResDto getProductDetail (
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId
   ) throws Exception {
        return service.selectProductDetail(auId);
    }

    // 상품 등록
    @PostMapping("api/product/admin")
    @ResponseBody
    @Operation(summary = "상품 등록", description = "전달받은 상품의 정보를 등록합니다.")
    public void postProduct (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 정보") @RequestBody @Valid ProductPostReqDto model
    ) throws Exception {
        service.insertProduct(model);
    }

    // 상품 기본정보 수정
    @PatchMapping("api/product/admin/{id}/basic")
    @ResponseBody
    @Operation(summary = "상품 기본정보 수정", description = "전달받은 id의 상품 기본정보를 수정합니다.")
    public void patchProductBasic (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 기본정보") @RequestBody @Valid ProductBasicPatchReqDto model
    ) throws Exception {
        service.updateProductBasic(auId, model);
    }

    // 상품 배송정보 수정
    @PatchMapping("api/product/admin/{id}/shipping")
    @ResponseBody
    @Operation(summary = "상품 배송정보 수정", description = "전달받은 id의 상품 배송정보를 수정합니다.")
    public void patchProductShipping (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 배송정보") @RequestBody @Valid ProductShippingPatchReqDto model
    ) throws Exception {
        service.updateProductShipping(auId, model);
    }

    // 상품 섹션수정
    @PatchMapping("api/product/admin/{id}/section")
    @ResponseBody
    @Operation(summary = "상품 섹션정보 등록/수정", description = "전달받은 id의 상품 섹션정보를 등록/수정합니다.")
    public void patchProductSection (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 섹션정보") @RequestBody @Valid ProductSectionPatchReqDto model
    ) throws Exception {
        service.updateProductSection(auId, model);
    }

    // 상품 상세내용 수정
    @PatchMapping("api/product/admin/{id}/detailContent")
    @ResponseBody
    @Operation(summary = "상품 상세내용 수정", description = "전달받은 id의 상품 상세내용을 수정합니다.")
    public void patchProductDetailContent (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 상세내용정보") @RequestBody @Valid ProductDetailContentPatchReqDto model
    ) throws Exception {
        service.updateProductDetailContent(auId, model);
    }

    // 상품옵션 상세조회
    @GetMapping("api/product/admin/option/{id}")
    @Operation(summary = "상품 옵션 상세 조회", description = "상품 옵션을 조회합니다.")
    public ProductOptionGetResDto getProductOptionDetail (
            @Parameter(description = "옵션_ID") @PathVariable("id") UUID auId
   ) throws Exception {
        return service.selectProductOptionDetail(auId);
    }

    // 상품 옵션 등록
    @PostMapping("api/product/admin/{id}/option")
    @ResponseBody
    @Operation(summary = "상품 옵션 등록", description = "전달받은 상품의 옵션을 등록합니다.")
    public void postProductOption (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 옵션 정보") @RequestBody @Valid ProductOptionPostReqDto model
    ) throws Exception {
        service.insertProductOption(auId, model);
    }

}
