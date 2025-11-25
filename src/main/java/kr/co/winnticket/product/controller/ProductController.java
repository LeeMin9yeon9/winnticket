package kr.co.winnticket.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.winnticket.product.dto.ProductListGetResDto;
import kr.co.winnticket.product.dto.ProductPostReqDto;
import kr.co.winnticket.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "상품", description = "상품 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService service;

    // 상품 목록조회
    @GetMapping("")
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    public List<ProductListGetResDto> getProductList (
            @Parameter(description = "검색어") @RequestParam(value = "srchWord", required = false) String srchWord,
            @Parameter(description = "카테고리") @RequestParam(value = "categoryId", required = false) UUID categoryId,
            @Parameter(description = "상태 [ALL:전체, READY:준비중, ON_SALE:판매중, SOLD_OUT:품절, STOPPED:판매중단]") @RequestParam(value = "salesStatus", required = false) String salesStatus
    ) throws Exception {
        return service.selectProductList(srchWord, categoryId, salesStatus);
    }

    // 상품 등록
    @PostMapping("")
    @ResponseBody
    @Operation(summary = "상품 등록", description = "전달받은 상품의 정보를 등록합니다.")
    public void postProduct (
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "상품 정보") @RequestBody @Valid ProductPostReqDto model
    ) throws Exception {
        service.insertProduct(model);
    }

}
