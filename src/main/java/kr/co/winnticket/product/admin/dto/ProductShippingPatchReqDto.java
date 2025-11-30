package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 배송정보 수정] ProductShippingPatchReqDto")
public class ProductShippingPatchReqDto {
    @Schema(description = "배송정보")
    private String shippingInfo;

    @Schema(description = "보증정보")
    private String warrantyInfo;

    @Schema(description = "반품/교환정보")
    private String returnInfo;

    @Hidden
    @Schema(description = "아이디")
    private UUID id;
}
