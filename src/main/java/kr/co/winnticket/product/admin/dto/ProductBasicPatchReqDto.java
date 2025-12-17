package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.common.enums.SalesStatus;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 기본정보 수정] ProductBasicPatchReqDto")
public class ProductBasicPatchReqDto {
    @NotEmpty
    @Schema(description = "상품명")
    private String name;

    @Schema(description = "카테고리_ID")
    private UUID categoryId;

    @Schema(description = "파트너_ID")
    private UUID partnerId;

    @NotNull
    @Schema(description = "정가")
    private int price;

    @Schema(description = "할인가")
    private int discountPrice;

    @Schema(description = "재고수량")
    private int stock;

    @Schema(description = "판매상태")
    private SalesStatus salesStatus;

    @Schema(description = "상품설명")
    private String description;

    @Schema(description = "상품설명")
    private List<String> imageUrl;

    @Hidden
    @Schema(description = "아이디")
    private UUID id;
}
