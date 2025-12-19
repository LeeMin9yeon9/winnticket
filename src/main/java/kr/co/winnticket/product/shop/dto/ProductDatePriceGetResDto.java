package kr.co.winnticket.product.shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 날짜별 가격 조회] ProductDatePriceGetResDto")
public class ProductDatePriceGetResDto {
    @Schema(description = "옵션별가격ID")
    private UUID optionValueId;

    @Schema(description = "옵션별가격명")
    private String optionValueName;

    @Schema(description = "판매일자")
    private LocalDate priceDate;

    @Schema(description = "가격")
    private int price;

    @Schema(description = "할인가")
    private int discountPrice;
}
