package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.common.enums.OptionCode;
import kr.co.winnticket.common.enums.OptionPriceType;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 옵션 조회] ProductOptionGetResDto")
public class ProductOptionGetResDto {
    @NotNull
    @Schema(description = "옵션_ID")
    private UUID id;

    @Schema(description = "옵션명")
    private String name;

    @Schema(description = "옵션코드")
    private OptionCode code;
    
    @Schema(description = "옵션별가격타입")
    private OptionPriceType priceType;

    @Schema(description = "필수여부")
    private boolean required;

    @Schema(description = "옵션별가격")
    private List<ProductOptionValueGetResDto> values = new ArrayList<>();
}
