package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 옵션값 수정] ProductOptionValuePatchReqDto")
public class ProductOptionValuePatchReqDto {
    @Schema(description = "옵션별가격명")
    private String value;

    @Schema(description = "옵션별가격코드")
    private String code;

    @Schema(description = "옵션별가격재고")
    private int stock;

    @Schema(description = "옵션별가격")
    private int additionalPrice;

    @Schema(description = "원가")
    private int basePrice;

    @Hidden
    @Schema(description = "옵션값_ID")
    private UUID optionValueId;
}
