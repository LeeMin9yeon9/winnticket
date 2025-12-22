package kr.co.winnticket.cart.dto.mapperDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(title = "[SHOP > MAPPER 상품 옵션 조회] OptionValueViewDto")
public class OptionValueViewDto {

    @Schema(description = "상품 옵션 ID")
    private UUID id;

    @Schema(description = "상품 옵션 이름")
    private String optionName;

    @Schema(description = "상품 옵션 값")
    private String value;

    @Schema(description = "상품 추가 금액")
    private int additionalPrice;
}
