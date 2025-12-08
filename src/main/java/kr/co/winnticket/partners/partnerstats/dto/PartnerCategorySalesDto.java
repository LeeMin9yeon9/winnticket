package kr.co.winnticket.partners.partnerstats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[파트너 > 판매현황 카테고리별 매출] PartnerCategorySalesDto")
public class PartnerCategorySalesDto {

    @Schema(description = "카테고리명")
    private String category;

    @Schema(description = "매출액")
    private Long revenue;
}
