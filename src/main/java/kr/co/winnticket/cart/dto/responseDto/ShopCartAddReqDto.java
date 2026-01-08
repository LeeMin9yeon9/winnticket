package kr.co.winnticket.cart.dto.responseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.cart.dto.sessionDto.CartOptionSessionDto;
import lombok.Data;

import java.time.LocalDate;
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

    @Schema(description = "숙박 옵션값 ID (객실 타입)")
    private UUID stayOptionValueId;

    @Schema(description = "체크인 날짜")
    private LocalDate startDate;

    @Schema(description = "체크아웃 날짜")
    private LocalDate endDate;

    private List<CartOptionSessionDto> options;


}
