package kr.co.winnticket.cart.dto.mapperDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(title = "[ SHOP > MAPPER 상품 조회] ProductCartViewDto")
public class ProductCartViewDto {

    @Schema(description = "상품 ID")
    private UUID id;

    @Schema(description = "상품이름")
    private String name;

    @Schema(description = "상품이미지")
    private String imageUrl;

    @Schema(description = "상품 값")
    private int price;

    @Schema(description = "상품 할인 값")
    private Integer discountPrice;


}
