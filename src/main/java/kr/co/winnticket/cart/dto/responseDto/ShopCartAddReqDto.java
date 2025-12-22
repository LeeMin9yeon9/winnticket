package kr.co.winnticket.cart.dto.responseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.cart.dto.sessionDto.CartOptionSessionDto;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(title = "[SHOP > 장바구니 추가] ShopCartAddReqDto")
public class ShopCartAddReqDto {

    @NotNull
    @Schema(description = "상품ID")
    private UUID productId;

    @Min(1)
    @Schema(description = "상품수량")
    private int quantity;

    private List<CartOptionSessionDto> options;


}
