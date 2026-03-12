package kr.co.winnticket.integration.lscompany.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.integration.lscompany.dto.*;
import kr.co.winnticket.integration.lscompany.service.LsCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ApiResponse<LsProductResDto> getProducts() {

        return ApiResponse.success(service.getProducts());
    }

    @Operation(summary = "LS컴퍼니 티켓 발권")
    @PostMapping("/issue/{orderNo}")
    public ApiResponse<LsIssueResDto> issueTicket(
            @PathVariable String orderNo) {

        return ApiResponse.success(service.issueTicket(orderNo));
    }

    @Operation(summary = "LS 티켓 상태 조회")
    @PostMapping("/inquiry")
    public ApiResponse<LsStatusResDto> inquiry(
            @RequestParam String transactionId
    ) {
        return ApiResponse.success(service.inquiryTicket(transactionId));
    }

    @Operation(summary = "LS 티켓 취소")
    @PostMapping("/cancel")
    public ApiResponse <List<LsCancelResDto>> cancel(
            @RequestParam String orderNumber
    ) {

        return ApiResponse.success(service.cancelTicket(orderNumber));
    }

    @Operation(summary = "LS 티켓 문자 재전송")
    @PostMapping("/resend")
    public ApiResponse<LsResendResDto> resendTicket(
            @RequestParam String orderNumber
    ){
        return ApiResponse.success(service.resendTicket(orderNumber)
        );
    }


}
