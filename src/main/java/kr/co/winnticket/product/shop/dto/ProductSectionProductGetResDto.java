package kr.co.winnticket.product.shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import kr.co.winnticket.common.enums.SalesStatus;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 섹션별 상품 조회] ProductSectionProductGetResDto")
public class ProductSectionProductGetResDto {
    @NotEmpty
    @Schema(description = "상품코드")
    private String code;

    @Schema(description = "상품명")
    private String name;

    @Schema(description = "정가")
    private int price;

    @Schema(description = "대표이미지")
    private String image;

    @Schema(description = "판매가")
    private int discountPrice;

    @Schema(description = "할인율")
    private int discountRate;

    @Schema(description = "판매상태")
    private SalesStatus salesStatus;

    @Schema(description = "사용기한")
    private String usagePeriod;
}
