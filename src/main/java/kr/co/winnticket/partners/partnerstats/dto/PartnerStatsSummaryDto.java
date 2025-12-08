package kr.co.winnticket.partners.partnerstats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[파트너 > 판매현황 총 매출,주문,판매,금액] PartnerStatsSummaryDto")
public class PartnerStatsSummaryDto {

    @Schema(description = "총 매출")
    private Long totalRevenue;

    @Schema(description = "총 주문 수")
    private Integer totalOrders;

    @Schema(description = "총 판매된 티켓 수")
    private Integer totalTickets;

    @Schema(description = "평균 주문 금액")
    private Integer averageOrderValue;


}
