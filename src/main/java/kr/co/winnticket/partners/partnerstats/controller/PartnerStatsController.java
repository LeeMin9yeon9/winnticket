package kr.co.winnticket.partners.partnerstats.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.partners.partnerstats.dto.*;
import kr.co.winnticket.partners.partnerstats.service.PartnerStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "파트너 판매현황", description = "파트너 상세 > 파트너 판매현황")
@RequestMapping("/api/partners/{partnerId}/stats")
public class PartnerStatsController {

    private final PartnerStatsService service;

    @GetMapping("/summary")
    @Operation(summary = "파트너 판매 현황", description = "총 매출, 총 주문 수, 총 티켓 판매 수, 평균 금액을 조회합니다.")
    public PartnerStatsSummaryDto getSummary(
            @PathVariable String partnerId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ){
        return service.getSummary(partnerId,startDate,endDate);
    }

    @GetMapping("/daily")
    @Operation(summary = "파트너 일별 매출" , description = "기간 내 일별 매출을 조회할 수 있습니다.")
    public List<PartnerDailySalesDto> getDaily(
            @PathVariable String partnerId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ){
        return service.getDailySales(partnerId,startDate,endDate);
    }

    @GetMapping("/category")
    @Operation(summary = "파트너 카테고리 매출" , description = "기간 내 카테고리 매출을 조회할 수 있습니다.")
    public List<PartnerCategorySalesDto> getCategory(
            @PathVariable String partnerId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ){
        return service.getCategorySales(partnerId,startDate,endDate);
    }

    @GetMapping("/topProducts")
    @Operation(summary = "파트너 TOP 판매 상품" , description = "기간 내 매출 상위 상품을 확인할 수 있습니다.")
    public List<PartnerTopProductsDto> getTopProducts(
            @PathVariable String partnerId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ){
        return service.getTopProducts(partnerId,startDate,endDate);
    }



}
