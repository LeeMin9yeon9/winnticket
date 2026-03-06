package kr.co.winnticket.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[대시보드 > 파트너매출현황 Dto] DashboardPartnerSalesDto")
public class DashboardPartnerSalesDto {

    @Schema(description="파트너명")
    private String partnerName;

    @Schema(description="상품 수")
    private int productCount;

    @Schema(description="판매티켓 수")
    private int orderCount;

    @Schema(description="매출")
    private Long salesAmount;

    @Schema(description = "순이익")
    private long netProfit;
}