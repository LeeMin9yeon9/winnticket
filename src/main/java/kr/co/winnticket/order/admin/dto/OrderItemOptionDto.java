package kr.co.winnticket.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.winnticket.common.enums.ProductType;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
public class OrderItemOptionDto {
    @Schema(description = "옵션값id")
    private UUID optionValueId;

    @Schema(description = "상품타입")
    private ProductType productType;

    @Schema(description = "수량")
    private Integer quantity;
}
