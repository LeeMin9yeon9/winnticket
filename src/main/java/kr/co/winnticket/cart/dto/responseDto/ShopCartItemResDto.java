package kr.co.winnticket.cart.dto.responseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(title = "[SHOP > 장바구니 항목 리스트] ShopCartItemResDto")
public class ShopCartItemResDto {

    @Schema(description = "장바구니ID")
    private UUID id;

    @Schema(description = "상품 ID")
    private UUID productId;

    @Schema(description = "상품이름")
    private String productName;

    @Schema(description = "상품이미지")
    private List<String> imageUrl;

    @Schema(description = "상품 수량")
    private int quantity;

    @Schema(description = "상품 정가")
    private int unitOriginPrice;

    @Schema(description = "할인금액")
    private int discountPrice;

    @Schema(description = "실제 결제 가격")
    private int unitFinalPrice;

    @Schema(description = "최종 상품 금액")
    private int itemTotalPrice;

    private List<ShopCartOptionResDto> options;
}
