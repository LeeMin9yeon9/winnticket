package kr.co.winnticket.product.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.common.enums.SmsTemplateCode;
import kr.co.winnticket.product.admin.dto.ProductPostReqDto;
import kr.co.winnticket.product.admin.dto.ProductSmsTemplateDto;
import kr.co.winnticket.product.admin.dto.ProductSmsTemplateSaveReqDto;
import kr.co.winnticket.product.admin.service.ProductSmsTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/product/")
public class ProductSmsTemplateController {

    private final ProductSmsTemplateService service;

    // 상품별 템플릿 조회
    @GetMapping("/{id}/sms-templates")
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "템플릿 목록 조회", description = "상품별 템플릿 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ProductSmsTemplateDto>>> getTemplates(
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.getTemplates(auId))
        );
    }

    // 기본 템플릿 조회
    @GetMapping("/sms-templates/default")
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "기본 템플릿 조회", description = "기본 템플릿을 조회합니다.")
    public ResponseEntity<ApiResponse<ProductSmsTemplateDto>> getDefault(
            @Parameter(description = "템플릿코드[ORDER_RECEIVED:주문접수, PAYMENT_CONFIRMED:입금확인, TICKET_ISSUED:발권완료, ORDER_CANCELLED:취소완료]") @RequestParam(value = "code") SmsTemplateCode code
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.getDefaultTemplate(code))
        );
    }

    // 템플릿 수정
    @PostMapping("/{id}/sms-templates")
    @Tag(name = "상품_관리자", description = "상품 관리")
    @Operation(summary = "템플릿 수정", description = "템플릿을 수정합니다.")
    public ResponseEntity<ApiResponse<String>> save(
            @Parameter(description = "상품 ID") @PathVariable("id") UUID auId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "템플릿 정보") @RequestBody @Valid ProductSmsTemplateSaveReqDto model
    ) throws Exception {
        service.saveTemplates(auId, model.getTemplates());

        return ResponseEntity.ok(
                ApiResponse.success("수정 성공", auId.toString())
        );
    }
}
