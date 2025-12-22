package kr.co.winnticket.cart.dto.responseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "[SHOP > 장바구니 조회] ShopCartResDto")
public class ShopCartResDto {

    private List<ShopCartItemResDto> items;

    @Schema(description = "할인 전 전체 주문 금액")
    private int orderAmount;

    @Schema(description = "전체 할인 금액")
    private int discountAmount;

    @Schema(description = "실제 결제 예정 금액")
    private int finalAmount;
}
