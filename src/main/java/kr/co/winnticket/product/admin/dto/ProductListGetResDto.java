package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.common.enums.SalesStatus;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 목록 조회] ProductListGetResDto")
public class ProductListGetResDto {
    @NotNull
    @Schema(description = "상품_ID")
    private UUID id;

    @Schema(description = "대표이미지")
    private String image;
    
    @Schema(description = "상품코드")
    private String code;

    @Schema(description = "상품명")
    private String name;

    @Schema(description = "카테고리_ID")
    private UUID categoryId;

    @Schema(description = "파트너_ID")
    private UUID partnerId;

    @Schema(description = "정가")
    private int price;

    @Schema(description = "판매가")
    private int discountPrice;

    @Schema(description = "판매상태")
    private SalesStatus salesStatus;

    @Schema(description = "재고")
    private int stock;

    @Schema(description = "활성화여부")
    private boolean visible;
}
