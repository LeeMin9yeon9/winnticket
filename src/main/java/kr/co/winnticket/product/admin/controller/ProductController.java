package kr.co.winnticket.product.admin.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.product.admin.dto.*;
import kr.co.winnticket.product.admin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product")
public class ProductController {
    private final ProductService service;

    // 상품 목록조회
    @GetMapping
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ProductListGetResDto>>> getProductList (
            @Parameter(description = "검색어") @RequestParam(value = "srchWord", required = false) String srchWord,
            @Parameter(description = "카테고리") @RequestParam(value = "categoryId", required = false) UUID categoryId,
            @Parameter(description = "상태 [ALL:전체, READY:준비중, ON_SALE:판매중, SOLD_OUT:품절, STOPPED:판매중단]") @RequestParam(value = "salesStatus", required = false) String salesStatus
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectProductList(srchWord, categoryId, salesStatus))
        );
    }

    // 상품 기본정보 조회
    @GetMapping("/{id}")
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 기본정보 조회", description = "전달받은 id의 상품의 상세정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ProductDetailGetResDto>> getProductDetail (
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId
   ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectProductDetail(auId))
        );
    }

    // 상품 등록
    @PostMapping
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 등록", description = "전달받은 상품의 정보를 등록합니다.")
    public ResponseEntity<ApiResponse<ProductPostReqDto>> postProduct (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 정보") @RequestBody @Valid ProductPostReqDto model
    ) throws Exception {
        service.insertProduct(model);

        return ResponseEntity.ok(
                ApiResponse.success("등록 성공", model)
        );
    }

    // 상품 기본정보 수정
    @PatchMapping("/{id}/basic")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 기본정보 수정", description = "전달받은 id의 상품 기본정보를 수정합니다.")
    public ResponseEntity<ApiResponse<ProductBasicPatchReqDto>> patchProductBasic (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 기본정보") @RequestBody @Valid ProductBasicPatchReqDto model
    ) throws Exception {
        service.updateProductBasic(auId, model);
        model.setId(auId);

        return ResponseEntity.ok(
                ApiResponse.success("수정 성공", model)
        );
    }

    // 상품 배송정보 수정
    @PatchMapping("/{id}/shipping")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 배송정보 수정", description = "전달받은 id의 상품 배송정보를 수정합니다.")
    public ResponseEntity<ApiResponse<ProductShippingPatchReqDto>> patchProductShipping (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 배송정보") @RequestBody @Valid ProductShippingPatchReqDto model
    ) throws Exception {
        service.updateProductShipping(auId, model);
        model.setId(auId);

        return ResponseEntity.ok(
                ApiResponse.success("수정 성공", model)
        );
    }

    // 상품 섹션수정
    @PatchMapping("/{id}/section")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 섹션정보 등록/수정", description = "전달받은 id의 상품 섹션정보를 등록/수정합니다.")
    public ResponseEntity<ApiResponse<ProductSectionPatchReqDto>> patchProductSection (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 섹션정보") @RequestBody @Valid ProductSectionPatchReqDto model
    ) throws Exception {
        service.updateProductSection(auId, model);
        model.setId(auId);

        return ResponseEntity.ok(
                ApiResponse.success("수정 성공", model)
        );
    }

    // 상품 활성화여부 수정
    @PatchMapping("/{id}/visible")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 활성화여부 수정", description = "전달받은 id의 상품 활성하여부를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> patchProductVisible (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId,
            @Parameter(description = "활성화여부") @RequestParam(value = "visible") boolean visible
    ) throws Exception {
        service.updateProductVisible(auId, visible);
        String id = auId.toString();

        return ResponseEntity.ok(
                ApiResponse.success("수정 성공", id)
        );
    }

    // 상품 상세내용 수정
    @PatchMapping("/{id}/detailContent")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 상세내용 수정", description = "전달받은 id의 상품 상세내용을 수정합니다.")
    public ResponseEntity<ApiResponse<String>> patchProductDetailContent (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 상세정보") @RequestBody @Valid ProductDetailContentPatchReqDto model
    ) throws Exception {
        service.updateProductDetailContent(auId, model);
        String id = auId.toString();

        return ResponseEntity.ok(
                ApiResponse.success("수정 성공", id)
        );
    }

    // 상품 삭제
    @DeleteMapping("/{id}")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 삭제", description = "전달받은 id의 상품을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>>  deleteProduct (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        service.deleteProduct(auId);
        String id = auId.toString();

        return ResponseEntity.ok(
                ApiResponse.success("삭제 성공", id)
        );
    }
    
    // 상품옵션 상세조회
    @GetMapping("/option/{id}")
    @Tag(name = "옵션", description = "옵션 관리")
    @Operation(summary = "상품 옵션 상세 조회", description = "상품 옵션을 조회합니다.")
    public ResponseEntity<ApiResponse<ProductOptionGetResDto>> getProductOptionDetail (
            @Parameter(description = "옵션_ID") @PathVariable("id") UUID auId
   ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectProductOptionDetail(auId))
        );
    }

    // 상품 옵션 등록
    @PostMapping("/{id}/option")
    @ResponseBody
    @Tag(name = "옵션", description = "옵션 관리")
    @Operation(summary = "상품 옵션 등록", description = "전달받은 id의 상품 옵션을 등록합니다.")
    public ResponseEntity<ApiResponse<ProductOptionPostReqDto>> postProductOption (
            @Parameter(description = "상품_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 옵션 정보") @RequestBody @Valid ProductOptionPostReqDto model
    ) throws Exception {
        service.insertProductOption(auId, model);

        return ResponseEntity.ok(
                ApiResponse.success("등록 성공", model)
        );
    }

    // 상품 옵션 수정
    @PatchMapping("/option/{id}")
    @ResponseBody
    @Tag(name = "옵션", description = "옵션 관리")
    @Operation(summary = "상품 옵션 수정", description = "전달받은 id의 옵션을 수정합니다.")
    public ResponseEntity<ApiResponse<ProductOptionPatchReqDto>> patchProductOption (
            @Parameter(description = "옵션_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 옵션 정보") @RequestBody @Valid ProductOptionPatchReqDto model
    ) throws Exception {
        service.updateProductOption(auId, model);
        model.setOptionId(auId);

        return ResponseEntity.ok(
                ApiResponse.success("수정 성공",model)
        );
    }

    // 상품 옵션 삭제
    @DeleteMapping("/option/{id}")
    @ResponseBody
    @Tag(name = "옵션", description = "옵션 관리")
    @Operation(summary = "상품 옵션 삭제", description = "전달받은 id의 옵션을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>>  deleteProductOption (
            @Parameter(description = "옵션_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        service.deleteProductOption(auId);
        String id = auId.toString();

        return ResponseEntity.ok(
                ApiResponse.success("삭제 성공", id)
        );
    }

    // 상품 옵션값 수정
    @PatchMapping("/optionValue/{id}")
    @ResponseBody
    @Tag(name = "옵션", description = "옵션 관리")
    @Operation(summary = "상품 옵션값 수정", description = "전달받은 id의 옵션값을 수정합니다.")
    public ResponseEntity<ApiResponse<ProductOptionValuePatchReqDto>> patchProductOptionValue (
            @Parameter(description = "옵션값_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 옵션값 정보") @RequestBody @Valid ProductOptionValuePatchReqDto model
    ) throws Exception {
        service.updateProductOptionValue(auId, model);
        model.setOptionValueId(auId);

        return ResponseEntity.ok(
                ApiResponse.success("수정 성공",model)
        );
    }

    // 상품 기간 등록
    @PostMapping("/period")
    @ResponseBody
    @Tag(name = "기간", description = "기간 관리")
    @Operation(summary = "상품 기간 등록", description = "전달받은 id의 상품 기간을 등록합니다.")
    public ResponseEntity<ApiResponse<ProductPeriodPostReqDto>> postProductPeriod (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 기간 정보") @RequestBody @Valid ProductPeriodPostReqDto model
    ) throws Exception {
        service.insertProductPeriod(model);

        return ResponseEntity.ok(
                ApiResponse.success("등록 성공", model)
        );
    }

    // 상품 기간 삭제
    @DeleteMapping("/period/{id}/{groupNo}")
    @ResponseBody
    @Tag(name = "기간", description = "기간 관리")
    @Operation(summary = "상품 기간 삭제", description = "전달받은 id의 상품 기간을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteProductPeriod (
            @Parameter(description = "옵션값_ID") @PathVariable("id") UUID auId,
            @Parameter(description = "그룹번호") @PathVariable("groupNo") int groupNo
    ) throws Exception {
        service.deleteProductPeriod(auId, groupNo);
        String id = auId.toString();

        return ResponseEntity.ok(
                ApiResponse.success("등록 성공", id)
        );
    }

    // 섹션 목록 조회
    @GetMapping("/section/list")
    @Tag(name = "섹션", description = "섹션 관리")
    @Operation(summary = "섹션 목록 조회", description = "섹션 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<SectionListGetResDto>>> getSectionList (
    ) throws Exception {

        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectSectionList())
        );
    }

    // 활성화 섹션 목록 조회
    @GetMapping("/section/list/active")
    @Tag(name = "섹션", description = "섹션 관리")
    @Operation(summary = "활성화 섹션 목록 조회", description = "활성화된 섹션 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<SectionListActiveGetResDto>>> getSectionListActive (
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectSectionListActive())
        );
    }

    // 섹션 상세 조회
    @GetMapping("/section/{id}")
    @Tag(name = "섹션", description = "섹션 관리")
    @Operation(summary = "섹션 상세 조회", description = "전달받은 id의 섹션을 조회합니다.")
    public ResponseEntity<ApiResponse<SectionDetailGetResDto>> getSectionDetailActive (
            @Parameter(description = "섹션 ID") @PathVariable("id") UUID auId
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectSectionDetail(auId))
        );
    }

    // 섹션 등록
    @PostMapping("/section")
    @ResponseBody
    @Tag(name = "섹션", description = "섹션 관리")
    @Operation(summary = "섹션 등록", description = "섹션을 등록합니다.")
    public ResponseEntity<ApiResponse<SectionPostReqDto>> postSection (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "섹션 정보") @RequestBody @Valid SectionPostReqDto model
    ) throws Exception {
        service.insertSection(model);

        return ResponseEntity.ok(
                ApiResponse.success("등록 성공", model)
        );
    }

    // 섹션 수정
    @PatchMapping("/section/{id}")
    @ResponseBody
    @Tag(name = "섹션", description = "섹션 관리")
    @Operation(summary = "섹션 수정", description = "전달받은 id의 섹션을 수정합니다.")
    public ResponseEntity<ApiResponse<SectionPatchReqDto>> patchSection (
            @Parameter(description = "섹션_ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "섹션 정보") @RequestBody @Valid SectionPatchReqDto model
    ) throws Exception {
        service.updateSection(auId, model);
        model.setId(auId);

        return ResponseEntity.ok(
                ApiResponse.success("수정 성공", model)
        );
    }

    // 섹션 삭제
    @DeleteMapping("/section/{id}")
    @ResponseBody
    @Tag(name = "섹션", description = "섹션 관리")
    @Operation(summary = "섹션 삭제", description = "전달받은 id의 섹션을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteSection (
            @Parameter(description = "섹션_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        service.deleteSection(auId);
        String id = auId.toString();

        return ResponseEntity.ok(
                ApiResponse.success("삭제 성공", id)
        );
    }

    // 상품 채널별 할인 목록 조회
    @Hidden
    @GetMapping("/{id}/channelDiscounts")
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 채널별 할인 목록 조회", description = "전달받은 id의 상품의 채널별 할인 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ProductChannelDiscountListGetResDto>>> getProductChannelDiscountsList (
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId,
            @Parameter(description = "채널명") @RequestParam(value = "channelName", required = false) String channelName,
            @Parameter(description = "상태 [ALL:전체, ENABLED:활성, DISABLED:비활성, UPCOMING:예정, ENDED:만료]") @RequestParam(value = "status") String status,
            @Parameter(description = "기간 [ALL:전체, ONGOING:진행중, UPCOMING:예정, ENDED:만료]") @RequestParam(value = "period") String period
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectProductChannelDiscountsList(auId, channelName, status, period))
        );
    }

    // 상품 채널별 할인 상세조회
    @Hidden
    @GetMapping("/{id}/channelDiscounts/{discountId}")
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 채널별 할인 상세 조회", description = "전달받은 id의 상품의 채널별 할인을 상세조회합니다.")
    public ResponseEntity<ApiResponse<ProductChannelDiscountDetailGetResDto>> getProductChannelDiscountsDetail (
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId,
            @Parameter(description = "채널별할인 ID") @PathVariable("discountId") UUID discountId
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectProductChannelDiscountsDetail(auId, discountId))
        );
    }

    // 상품 채널별 할인 등록
    @Hidden
    @PostMapping("/{id}/channelDiscounts")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 채널별 할인 등록", description = "상품 채널별 할인을 등록합니다.")
    public ResponseEntity<ApiResponse<ProductChannelDiscountPostReqDto>> postProductChannelDiscount (
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "채널별할인 정보") @RequestBody @Valid ProductChannelDiscountPostReqDto model
    ) throws Exception {
        service.insertProductChannelDiscount(auId, model);

        return ResponseEntity.ok(
                ApiResponse.success("등록 성공", model)
        );
    }

    // 상품 채널별 할인 수정
    @Hidden
    @PatchMapping("/{id}/channelDiscounts/{discountId}")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 채널별 할인 수정", description = "상품 채널별 할인을 수정합니다.")
    public ResponseEntity<ApiResponse<ProductChannelDiscountPatchReqDto>> patchProductChannelDiscount (
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId,
            @Parameter(description = "채널별할인 ID") @PathVariable("discountId") UUID discountId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "채널별할인 정보") @RequestBody @Valid ProductChannelDiscountPatchReqDto model
    ) throws Exception {
        service.updateProductChannelDiscount(auId, discountId, model);

        return ResponseEntity.ok(
                ApiResponse.success("수정 성공", model)
        );
    }

    // 상품 채널별 할인 삭제
    @Hidden
    @DeleteMapping("/{id}/channelDiscounts/{discountId}")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 채널별 할인 삭제", description = "상품 채널별 할인을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteProductChannelDiscount (
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId,
            @Parameter(description = "채널별할인 ID") @PathVariable("discountId") UUID discountId
    ) throws Exception {
        service.deleteProductChannelDiscount(auId, discountId);

        return ResponseEntity.ok(
                ApiResponse.success("삭제 성공", discountId.toString())
        );
    }

    // 상품 채널별 할인 활성화여부 수정
    @Hidden
    @PatchMapping("/{id}/channelDiscounts/{discountId}/isActive")
    @ResponseBody
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "상품 채널별 할인 활성화여부 수정", description = "상품 채널별 할인 활성화여부를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> patchProductChannelDiscountIsActive (
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId,
            @Parameter(description = "채널별할인 ID") @PathVariable("discountId") UUID discountId,
            @Parameter(description = "활성화여부") @RequestParam(value = "isActive") boolean abIsActive
    ) throws Exception {
        service.updateProductChannelDiscountIsActive(auId, discountId, abIsActive);

        return ResponseEntity.ok(
                ApiResponse.success("수정 성공", discountId.toString())
        );
    }

    // 상품 채널별 가격 목록 조회
    @GetMapping("/{id}/channelPrices")
    @Tag(name = "채널별가격", description = "채널별가격 관리")
    @Operation(summary = "상품 채널별 가격 목록 조회", description = "전달받은 id의 상품의 채널별 가격 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ProductChannelPriceListResDto>>> getProductChannelPriceList (
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectProductChannelPriceList(auId))
        );
    }

    // 상품 채널별 가격 상세 조회
    @GetMapping("/{id}/channelPrices/{channelId}")
    @Tag(name = "채널별가격", description = "채널별가격 관리")
    @Operation(summary = "상품 채널별 가격 상세 조회", description = "전달받은 id의 상품의 채널별 가격을 상세 조회합니다.")
    public ResponseEntity<ApiResponse<ProductChannelPriceDetailResDto>> getProductChannelPriceDetail (
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId,
            @Parameter(description = "채널 ID") @PathVariable("channelId") UUID channelId
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.getProductChannelPriceDetail(auId, channelId))
        );
    }

    // 상품 채널별 가격 저장
    @PostMapping("/{id}/channelPrices/{channelId}")
    @ResponseBody
    @Tag(name = "채널별가격", description = "채널별가격 관리")
    @Operation(summary = "상품 채널별 가격 저장", description = "상품 채널별 가격을 저장합니다.")
    public ResponseEntity<ApiResponse<ProductChannelPriceSaveReqDto>> saveChannelPrice (
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId,
            @Parameter(description = "채널 ID") @PathVariable("channelId") UUID channelId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "채널별가격 정보") @RequestBody @Valid ProductChannelPriceSaveReqDto model
    ) throws Exception {
        service.saveChannelPrice(auId, channelId, model);

        return ResponseEntity.ok(
                ApiResponse.success("등록 성공", model)
        );
    }

    // 상품 채널별 활성화여부 수정
    @PatchMapping("/{id}/channelDiscounts/{channelId}/enable")
    @ResponseBody
    @Tag(name = "채널별가격", description = "채널별가격 관리")
    @Operation(summary = "상품 채널별 활성화여부 수정", description = "상품 채널별 활성화여부를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> patchProductChannelEnable (
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId,
            @Parameter(description = "채널 ID") @PathVariable("channelId") UUID channelId,
            @Parameter(description = "활성화여부") @RequestParam(value = "enable") boolean abEnable
    ) throws Exception {
        service.updateProductChannelEnable(auId, channelId, abEnable);

        return ResponseEntity.ok(
                ApiResponse.success("수정 성공", channelId.toString())
        );
    }
}
