package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.common.enums.OptionCode;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
public class ProductChannelOptionPriceGetResDto {
    @NotNull
    @Schema(description = "옵션_ID")
    private UUID optionId;

    @Schema(description = "옵션명")
    private String optionName;

    @Schema(description = "옵션코드")
    private OptionCode optionCode;

    @Schema(description = "옵션값별 가격")
    private List<ProductChannelOptionValuePriceGetResDto> optionsValues = new ArrayList<>();
}
