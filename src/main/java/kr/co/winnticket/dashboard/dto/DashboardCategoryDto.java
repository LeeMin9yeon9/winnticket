package kr.co.winnticket.dashboard.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[대시보드 > 카테고리별 상품 Dto] DashboardCategoryDto")
public class DashboardCategoryDto {

    @Schema(description="카테고리명")
    private String categoryName;

    @Schema(description="상품 수")
    private int productCount;


}
