package kr.co.winnticket.partners.partnerstats.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(title = "[파트너 > 판매현황 파트너 일별 매출] PartnerDailySalesDto")
public class PartnerDailySalesDto {
    @Schema(description = "YYYYMMDD", example = "20250101")
    private LocalDate date;

    @Schema(description = "일 매출")
    private Long revenue;
}
