package kr.co.winnticket.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[대시보드 > 티켓 재고 부족 Dto] DashboardCouponLowStockDto")
public class DashboardCouponLowStockDto {

    @Schema(description = "상품 ID")
    private String productId;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "옵션명")
    private String optionName;

    @Schema(description = "현재 판매 가능한 티켓 재고 수 (유효기간 안에 있는 ACTIVE 쿠폰)")
    private int activeCount;
}
