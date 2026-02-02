package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.common.enums.ProductType;
import kr.co.winnticket.common.enums.SalesStatus;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 등록] ProductPostReqDto")
public class ProductPostReqDto {
    @NotEmpty
    @Schema(description = "상품명", example = "샌드아트 체험")
    private String name;

    @NotNull
    @Schema(description = "상품타입", example = "NORMAL")
    private ProductType type;

    @NotNull
    @Schema(description = "카테고리_ID", example = "436370c5-f491-4772-976b-d016a69c4ddb")
    private UUID categoryId;

    @Schema(description = "파트너_ID", example = "f706edba-f358-442a-b34f-dcf48bffc412")
    private UUID partnerId;

    @Schema(description = "상품설명", example = "아이들이 참여하는 샌드아트 체험 키트")
    private String description;

    @Schema(description = "대표이미지")
    private List<String> imageUrl;

    @NotNull
    @Schema(description = "정가", example = "20000")
    private int price;

    @Schema(description = "할인가", example = "15000")
    private int discountPrice;

    @Schema(description = "판매상태", example = "READY")
    private SalesStatus salesStatus;

    @Schema(description = "재고수량", example = "100")
    private int stock;

    @Schema(description = "판매시작일", example = "2025-01-01")
    private LocalDate salesStartDate;

    @Schema(description = "판매종료일", example = "2025-12-31")
    private LocalDate salesEndDate;

    @Schema(description = "활성화여부", example = "true")
    private boolean visible;

    @Schema(description = "지역코드", example = "1")
    private String regionCode;

    @Schema(description = "티켓타입", example = "01")
    private String ticketType;

    @Schema(description = "표시순서", example = "01")
    private String displayOrder;

    @Hidden
    private UUID id;
}
