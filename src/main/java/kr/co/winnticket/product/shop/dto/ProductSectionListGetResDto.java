package kr.co.winnticket.product.shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 섹션별 상품 조회] ProductSectionListGetResDto")
public class ProductSectionListGetResDto {
    @Schema(description = "섹션 ID")
    private UUID sectionId;

    @Schema(description = "섹션 코드")
    private String sectionCode;

    @Schema(description = "섹션명")
    private String sectionName;

    @Schema(description = "섹션별 상품 목록")
    private List<ProductSectionProductGetResDto> products;
}
