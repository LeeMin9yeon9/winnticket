package kr.co.winnticket.cart.dto.sessionDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(title = "[ SHOP > 장바구니 세션 ] CartItemSessionDto")
public class CartItemSessionDto {

    @Schema(description = "장바구니ID")
    private UUID id;

    @Schema(description = "상품ID")
    private UUID productId;

    @Schema(description = "상품 수")
    private int quantity;

    @Schema(description = "상품옵션")
    private List<CartOptionSessionDto> options;
}
