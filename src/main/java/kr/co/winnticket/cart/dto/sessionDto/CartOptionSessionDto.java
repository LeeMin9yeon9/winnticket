package kr.co.winnticket.cart.dto.sessionDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(title = "[SHOP > 장바구니 옵션 세션 ] CartOptionSessionDto")
public class CartOptionSessionDto {

    @Schema(description = "장바구니 옵션 ID")
    private UUID optionId;

    @Schema(description = "옵션 값")
    private UUID optionValueId;


}


