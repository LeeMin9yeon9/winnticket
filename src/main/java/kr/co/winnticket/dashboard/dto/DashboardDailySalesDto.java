package kr.co.winnticket.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
@Data
@Schema(title = "[대시보드 > 일별 판매 추이 Dto] DashboardResDto")
public class DashboardDailySalesDto {

    @Schema(description="날짜")
    private LocalDate date;

    @Schema(description="주문 수")
    private int orderCount;

    @Schema(description="매출")
    private Integer salesAmount;

    @Schema(description = "순이익")
    private long netProfit;
}
