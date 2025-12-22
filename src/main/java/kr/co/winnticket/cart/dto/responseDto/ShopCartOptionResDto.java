package kr.co.winnticket.cart.dto.responseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[SHOP > 장바구니 옵션] ShopCartOptionResDto")
public class ShopCartOptionResDto {

    @Schema(description = "옵션이름")
    private String optionName;

    @Schema(description = "옵션값")
    private String optionValue;
}
