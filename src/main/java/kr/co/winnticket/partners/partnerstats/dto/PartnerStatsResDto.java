package kr.co.winnticket.partners.partnerstats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "파트너 판매 통계 전체 응답")
public class PartnerStatsResDto {
    private PartnerStatsSummaryDto summary;                 // 판매요약

    private List<PartnerDailySalesDto> dailySales;          // 일별 매출
    private List<PartnerCategorySalesDto> categorySales;    // 카테고리 매출
    private List<PartnerTopProductsDto> topProducts;        // 상위 판매상품
}
