package kr.co.winnticket.product.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.winnticket.product.admin.dto.*;
import kr.co.winnticket.product.admin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    // 상품 목록조회
    @GetMapping("api/product/admin")
    @Tag(name = "상품_관리자", description = "상품 관리")
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
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 기본정보 조회", description = "전달받은 id의 상품의 상세정보를 조회합니다.")
    public ProductDetailGetResDto getProductDetail (
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId
   ) throws Exception {
        return service.selectProductDetail(auId);
    }

    // 상품 등록
    @PostMapping("api/product/admin")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 등록", description = "전달받은 상품의 정보를 등록합니다.")
    public void postProduct (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 정보") @RequestBody @Valid ProductPostReqDto model
    ) throws Exception {
        service.insertProduct(model);
    }

    // 상품 기본정보 수정
    @PatchMapping("api/product/admin/{id}/basic")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
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
    @Tag(name = "상품_관리자", description = "상품 관리")
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
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 섹션정보 등록/수정", description = "전달받은 id의 상품 섹션정보를 등록/수정합니다.")
    public void patchProductSection (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 섹션정보") @RequestBody @Valid ProductSectionPatchReqDto model
    ) throws Exception {
        service.updateProductSection(auId, model);
    }

    // 상품 상세내용 수정
    @PatchMapping(value="api/product/admin/{id}/detailContent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 상세내용 수정", description = "전달받은 id의 상품 상세내용을 수정합니다.")
    public void patchProductDetailContent (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID id,
            @RequestPart(value = "detailContent",required = false) String detailContent,
            @RequestPart(value = "existFileNames", required = false) List<String> existFileNames,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        service.updateProductDetailContent(id, detailContent, existFileNames, files);
    }

    // 상품옵션 상세조회
    @GetMapping("api/product/admin/option/{id}")
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 옵션 상세 조회", description = "상품 옵션을 조회합니다.")
    public ProductOptionGetResDto getProductOptionDetail (
            @Parameter(description = "옵션_ID") @PathVariable("id") UUID auId
   ) throws Exception {
        return service.selectProductOptionDetail(auId);
    }

    // 상품 옵션 등록
    @PostMapping("api/product/admin/{id}/option")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 옵션 등록", description = "전달받은 id의 상품 옵션을 등록합니다.")
    public void postProductOption (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 옵션 정보") @RequestBody @Valid ProductOptionPostReqDto model
    ) throws Exception {
        service.insertProductOption(auId, model);
    }

    // 상품 옵션 수정
    @PatchMapping("api/product/admin/option/{id}")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 옵션 수정", description = "전달받은 id의 옵션을 수정합니다.")
    public void patchProductOption (
            @Parameter(description = "옵션_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 옵션 정보") @RequestBody @Valid ProductOptionPatchReqDto model
    ) throws Exception {
        service.updateProductOption(auId, model);
    }

    // 상품 옵션 삭제
    @DeleteMapping("api/product/admin/option/{id}")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 옵션 삭제", description = "전달받은 id의 옵션을 삭제합니다.")
    public void patchProductOption (
            @Parameter(description = "옵션_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        service.deleteProductOption(auId);
    }

    // 섹션 목록 조회
    @GetMapping("api/product/admin/section/list")
    @Tag(name = "섹션", description = "섹션 관리")
    @Operation(summary = "섹션 목록 조회", description = "섹션 목록을 조회합니다.")
    public List<SectionListGetResDto> getSectionList (
    ) throws Exception {
        return service.selectSectionList();
    }

    // 활성화 섹션 목록 조회
    @GetMapping("api/product/admin/section/list/active")
    @Tag(name = "섹션", description = "섹션 관리")
    @Operation(summary = "활성화 섹션 목록 조회", description = "활성화된 섹션 목록을 조회합니다.")
    public List<SectionListActiveGetResDto> getSectionListActive (
    ) throws Exception {
        return service.selectSectionListActive();
    }

    // 섹션 상세 조회
    @GetMapping("api/product/admin/section/{id}")
    @Tag(name = "섹션", description = "섹션 관리")
    @Operation(summary = "섹션 상세 조회", description = "전달받은 id의 섹션을 조회합니다.")
    public SectionDetailGetResDto getSectionDetailActive (
            @Parameter(description = "섹션 ID") @PathVariable("id") UUID auId
    ) throws Exception {
        return service.selectSectionDetail(auId);
    }

    // 섹션 등록
    @PostMapping("api/product/admin/section")
    @ResponseBody
    @Tag(name = "섹션", description = "섹션 관리")
    @Operation(summary = "섹션 등록", description = "섹션을 등록합니다.")
    public void postSection (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "섹션 정보") @RequestBody @Valid SectionPostReqDto model
    ) throws Exception {
        service.insertSection(model);
    }

    // 섹션 수정
    @PatchMapping("api/product/admin/section/{id}")
    @ResponseBody
    @Tag(name = "섹션", description = "섹션 관리")
    @Operation(summary = "섹션 수정", description = "전달받은 id의 섹션을 수정합니다.")
    public void patchSection (
            @Parameter(description = "섹션_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "섹션 정보") @RequestBody @Valid SectionPatchReqDto model
    ) throws Exception {
        service.updateSection(auId, model);
    }

    // 섹션 삭제
    @DeleteMapping("api/product/admin/section/{id}")
    @ResponseBody
    @Tag(name = "섹션", description = "섹션 관리")
    @Operation(summary = "섹션 삭제", description = "전달받은 id의 섹션을 삭제합니다.")
    public void deleteSection (
            @Parameter(description = "섹션_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        service.deleteSection(auId);
    }

}
