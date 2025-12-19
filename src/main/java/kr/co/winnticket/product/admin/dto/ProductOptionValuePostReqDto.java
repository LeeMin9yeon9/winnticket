package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.winnticket.common.enums.OptionPriceType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 옵션별 가격 등록] ProductOptionValuePostReqDto")
public class ProductOptionValuePostReqDto {
    @Schema(description = "옵션별가격명")
    private String value;

    @Schema(description = "옵션별가격코드")
    private String code;

    @Schema(description = "옵션별가격타입")
    private OptionPriceType priceType;

    @Schema(description = "옵션별가격")
    private int additionalPrice;
}
