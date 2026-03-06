package kr.co.winnticket.dashboard.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[대시보드 > 상위 판매 상품 Dto] DashboardResDto")
public class DashboardTopProductDto {

    @Schema(description="상품명")
    private String productName;

    @Schema(description="판매 수")
    private int orderCount;
}
