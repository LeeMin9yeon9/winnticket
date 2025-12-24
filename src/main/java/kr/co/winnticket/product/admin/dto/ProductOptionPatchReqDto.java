package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.winnticket.common.enums.OptionPriceType;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 옵션 수정] ProductOptionPatchReqDto")
public class ProductOptionPatchReqDto {
    @Schema(description = "옵션명")
    private String name;

    @Schema(description = "옵션코드")
    private String code;

    @Schema(description = "필수여부")
    private boolean required;
    
    @Schema(description = "옵션별가격타입")
    private OptionPriceType priceType;

    @Schema(description = "옵션별가격등록")
    private List<ProductOptionValuePostReqDto> valuesInsert = new ArrayList<>();

    @Schema(description = "옵션별가격삭제")
    private List<UUID> deleteValueIds;

    @Hidden
    @Schema(description = "옵션_ID")
    private UUID optionId;
}
