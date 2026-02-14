package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;
@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 채널별 상품 가격 등록] ProductChannelPriceSaveReqDto")
public class ProductChannelPriceSaveReqDto {
    @Schema(description = "기본가")
    private Integer basePrice;

    @Schema(description = "할인가")
    private Integer discountPrice;

    @Schema(description = "옵션별가격")
    private List<ProductChannelOptionPriceSaveReqDto> options;
}
