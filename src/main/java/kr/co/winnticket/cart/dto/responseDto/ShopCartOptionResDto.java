package kr.co.winnticket.cart.dto.responseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Schema(title = "[SHOP > 장바구니 옵션] ShopCartOptionResDto")
public class ShopCartOptionResDto {

    @Schema(description = "장바구니 옵션 ID")
    private UUID optionId;

    @Schema(description = "옵션 값")
    private UUID optionValueId;

    @Schema(description = "옵션이름")
    private String optionName;

    @Schema(description = "옵션값")
    private String optionValue;

    // 숙박
    @Schema(description = "숙박기간id")
    private List<UUID> stayPeriodIds;

    @Schema(description = "티켓그룹")
    private Integer groupNo;

    @Schema(description = "숙박 옵션값 ID (객실 타입)")
    private UUID stayOptionValueId;

    @Schema(description = "체크인 날짜")
    private LocalDate startDate;

    @Schema(description = "체크아웃 날짜")
    private LocalDate endDate;
}
