package kr.co.winnticket.integration.lscompany.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.integration.lscompany.dto.*;
import kr.co.winnticket.integration.lscompany.service.LsCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lscompany")
public class LsCompanyController {

    private final LsCompanyService service;

    @Operation(summary = "LS컴퍼니 시설정보 조회", description = "LS컴퍼니 시설정보 조회 API")
    @PostMapping("/place")
    public LsPlaceResDto getPlaces() {

        return service.getPlaces();
    }

    @Operation(summary = "LS컴퍼니 상품정보 조회")
    @PostMapping("/products")
    public ApiResponse<LsProductResDto> getProducts(
            @RequestParam(required = false) String productCode
    ) {
        return ApiResponse.success(service.getProducts(productCode));
    }

    @Operation(summary = "LS컴퍼니 티켓 발권")
    @PostMapping("/issue/{orderId}")
    public ApiResponse<LsIssueResDto> issueTicket(
            @PathVariable UUID orderId) {

        return ApiResponse.success(service.issueTicket(orderId));
    }

    @Operation(summary = "LS 티켓 상태 조회 (주문 기준)")
    @PostMapping("/inquiry/{orderId}")
    public ApiResponse<List<LsStatusResDto>> inquiry(
            @PathVariable UUID orderId
    ) {
        return ApiResponse.success(service.inquiryTicket(orderId));
    }

    @Operation(summary = "LS 티켓 취소")
    @PostMapping("/cancel/{orderId}")
    public ApiResponse <List<LsCancelResDto>> cancel(
            @PathVariable UUID orderId
    ) {

        return ApiResponse.success(service.cancelTicket(orderId));
    }

    @Operation(summary = "LS 티켓 문자 재전송")
    @PostMapping("/resend/{orderId}")
    public ApiResponse<List<LsResendResDto>> resendTicket(
            @PathVariable UUID orderId
    ) {
        return ApiResponse.success(service.resendTicket(orderId));
    }


}
