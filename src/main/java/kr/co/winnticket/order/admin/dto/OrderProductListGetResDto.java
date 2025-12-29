package kr.co.winnticket.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[주문 > 주문 상품 목록 조회] OrderProductListGetResDto")
public class OrderProductListGetResDto {
    @NotNull
    @Schema(description = "주문상품_ID")
    private UUID id;

    @Schema(description = "상품ID")
    private UUID productId;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "카테고리명")
    private String categoryName;

    @Schema(description = "파트너명")
    private String partnerName;

    @Schema(description = "옵션명")
    private String optionName;

    @Schema(description = "수량")
    private int quantity;

    @Schema(description = "단가")
    private int unitPrice;

    @Schema(description = "소계")
    private int totalPrice;
}
