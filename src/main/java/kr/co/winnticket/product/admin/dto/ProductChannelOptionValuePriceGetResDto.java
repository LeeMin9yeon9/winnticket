package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;
@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 채널별 옵션값가격 목록 조회] ProductChannelOptionValuePriceGetResDto")
public class ProductChannelOptionValuePriceGetResDto {
    @NotNull
    @Schema(description = "옵션깂_ID")
    private UUID optionvalueId;

    @Schema(description = "옵션값명")
    private String optionvalueName;

    @Schema(description = "추가금액")
    private int additionalPrice;
}
