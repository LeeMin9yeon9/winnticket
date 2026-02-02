package kr.co.winnticket.partners.partnerproduct.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(title = "[파트너 > 파트너 적용상품 목록 응답 DTO] PartnerProductResDto")
public class PartnerProductListResDto {
    @Schema(description = "상품 ID")
    private UUID productId;

    @Schema(description = "옵션값 ID (없으면 null)")
    private UUID optionValueId;

    @Schema(description = "상품 대표 이미지 URL")
    private String imageUrl;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "옵션명")
    private String optionValue;

    @Schema(description = "카테고리명")
    private String categoryName;

    @Schema(description = "최종 판매 가격")
    private Integer price;

    @Schema(description = "재고")
    private Integer stock;

    @Schema(description = "판매 수량")
    private Long salesCount;

    @Schema(description = "총 매출")
    private Long totalSalesAmount;

    @Schema(description = "판매 상태")
    private String salesStatus;
}
