package kr.co.winnticket.partners.partnerstats.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[파트너 > 판매현황 상위 판매 상품 TOP3] PartnerTopProductsDto")
public class PartnerTopProductsDto {
    @Hidden
    @Schema(description = "상품 ID")
    private String productId;

    @Schema(description = "상품코드")
    private String productCode;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "매출액")
    private Long revenue;

    @Schema(description = "판매 티켓 수")
    private Integer tickets;
}
