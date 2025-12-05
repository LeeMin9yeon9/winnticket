package kr.co.winnticket.partners.partnerstats.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "파트너 일별 매출")
public class PartnerDailySalesDto {
    @Schema(description = "YYYY-MM-DD", example = "2025-01-01")
    private String date;

    @Schema(description = "일 매출")
    private Long revenue;
}
