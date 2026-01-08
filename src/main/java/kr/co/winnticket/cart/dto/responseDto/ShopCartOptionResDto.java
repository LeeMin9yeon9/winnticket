package kr.co.winnticket.cart.dto.responseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
}
