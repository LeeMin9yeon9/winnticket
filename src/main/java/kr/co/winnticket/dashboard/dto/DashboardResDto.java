package kr.co.winnticket.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "[대시보드 > 대시보드 전체 조회 Dto] DashboardResDto")
public class DashboardResDto {

    @Schema(description = "전체 등록 상품 수")
    private int productCount;

    @Schema(description = "판매중 상품 수")
    private int onSaleProductCount;

    @Schema(description = "준비중 상품 수")
    private int readyProductCount;



    @Schema(description = "전체 파트너 수")
    private int partnerCount;

    @Schema(description = "활성 파트너 수")
    private int activePartnerCount;

    @Schema(description = "비활성 파트너 수")
    private int inactivePartnerCount;



    @Schema(description = "전체 주문 건수")
    private int totalOrderCount;

    @Schema(description = "전체 완료주문 건수")
    private int orderCount;

    @Schema(description = "전체 취소 주문 건수")
    private int cancelOrderCount;



    @Schema(description = "이번 달 주문 건수")
    private int thisMonthTotalOrderCount;

    @Schema(description = "이번 달 완료 주문 건수")
    private int thisMonthOrderCount;

    @Schema(description = "이번 달 취소 주문 건수")
    private int thisMonthCancelOrderCount;



    @Schema(description = "파트너별 매출 현황")
    private List<DashboardPartnerSalesDto> partnerSales;

    @Schema(description = "카테고리별 상품 수")
    private List<DashboardCategoryDto> categoryProducts;

    @Schema(description = "상위 판매 상품")
    private List<DashboardTopProductDto> topProducts;

    @Schema(description = "일별 판매 추이")
    private List<DashboardDailySalesDto> dailySales;
}
