package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 옵션별가격 조회] ProductOptionGetResDto")
public class ProductOptionValueGetResDto {
    @NotNull
    @Schema(description = "옵션별가격_ID")
    private UUID id;

    @Schema(description = "옵션별가격코드")
    private String code;

    @Schema(description = "옵션별가격명")
    private String value;

    @Schema(description = "옵션별가격재고")
    private int stock;
    
    @Schema(description = "옵션별가격")
    private int additionalPrice;

    @Schema(description = "원가")
    private int basePrice;
}
