package kr.co.winnticket.cart.dto.responseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[SHOP > 장바구니 팝업 카운트] ShopCartCountResDto")
public class ShopCartCountResDto {
    @Schema(description = "장바구니 카운트")
    private int count;
}
