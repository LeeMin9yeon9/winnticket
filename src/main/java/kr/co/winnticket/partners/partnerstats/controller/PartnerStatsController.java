package kr.co.winnticket.partners.partnerstats.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.partners.partnerstats.dto.PartnerCategorySalesDto;
import kr.co.winnticket.partners.partnerstats.dto.PartnerDailySalesDto;
import kr.co.winnticket.partners.partnerstats.dto.PartnerStatsSummaryDto;
import kr.co.winnticket.partners.partnerstats.dto.PartnerTopProductsDto;
import kr.co.winnticket.partners.partnerstats.service.PartnerStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "파트너 판매현황", description = "파트너 상세 > 파트너 판매현황")
@RequestMapping("/api/admin/partners/{partnerId}/stats")
public class PartnerStatsController {

    private final PartnerStatsService service;

    @GetMapping("/summary")
    @Operation(summary = "파트너 판매 현황", description = "총 매출, 총 주문 수, 총 티켓 판매 수, 평균 금액을 조회합니다.")
    public ApiResponse<PartnerStatsSummaryDto> getSummary(
            @PathVariable String partnerId,
            @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate endDate
    ){
        return ApiResponse.success("판매 현황 조회 성공",
                service.getSummary(partnerId, startDate, endDate));
    }

    @GetMapping("/daily")
    @Operation(summary = "파트너 일별 매출" , description = "기간 내 일별 매출을 조회할 수 있습니다.")
    public ApiResponse<List<PartnerDailySalesDto>> getDaily(
            @PathVariable String partnerId,
            @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate endDate
    ){
        return ApiResponse.success("일별 매출 조회 성공",
                service.getDailySales(partnerId, startDate, endDate));
    }

    @GetMapping("/category")
    @Operation(summary = "파트너 카테고리 매출" , description = "기간 내 카테고리 매출을 조회할 수 있습니다.")
    public ApiResponse<List<PartnerCategorySalesDto>> getCategory(
            @PathVariable String partnerId,
            @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate endDate
    ){
        return ApiResponse.success("카테고리 매출 조회 성공",
                service.getCategorySales(partnerId, startDate, endDate));
    }

    @GetMapping("/topProducts")
    @Operation(summary = "파트너 TOP 판매 상품" , description = "기간 내 매출 상위 상품을 확인할 수 있습니다.")
    public ApiResponse<List<PartnerTopProductsDto>> getTopProducts(
            @PathVariable String partnerId,
            @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate endDate
    ){
        return ApiResponse.success("TOP 상품 조회 성공",
                service.getTopProducts(partnerId, startDate, endDate));
    }

}
