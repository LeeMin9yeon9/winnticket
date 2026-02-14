package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 채널별 옵션 가격 등록] ProductChannelOptionPriceSaveReqDto")
public class ProductChannelOptionPriceSaveReqDto {
    @Schema(description = "옵션_ID")
    private UUID optionId;

    @Schema(description = "옵션값_ID")
    private UUID optionValueId;

    @Schema(description = "추가금액")
    private Integer additionalPrice;
}
