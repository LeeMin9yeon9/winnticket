package kr.co.winnticket.cart.dto.mapperDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(title = "[SHOP > MAPPER 상품 옵션 조회] OptionValueViewDto")
public class OptionValueViewDto {

    @Schema(description = "옵션 ID")
    private UUID optionId;

    @Schema(description = "옵션 값 ID")
    private UUID optionValueId;

    @Schema(description = "옵션 이름")
    private String optionName;

    @Schema(description = "옵션 값")
    private String optionValue;

    @Schema(description = "상품 추가 금액")
    private int additionalPrice;
}
