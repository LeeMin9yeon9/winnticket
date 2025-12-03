package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.common.enums.SalesStatus;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 등록] ProductPostReqDto")
public class ProductPostReqDto {
    @NotEmpty
    @Schema(description = "상품명", example = "샌드아트 체험")
    private String name;

    @NotEmpty
    @Schema(description = "상품코드", example = "SD001")
    private String code;

    @NotNull
    @Schema(description = "카테고리_ID", example = "a5f0933c-51ac-4fe0-8e88-7f982b8a09c6")
    private UUID categoryId;

    @Schema(description = "파트너_ID", example = "6c820c44-f42d-47ad-8bbb-5675c482123a")
    private UUID partnerId;

    @Schema(description = "상품설명", example = "아이들이 참여하는 샌드아트 체험 키트")
    private String description;

    @Hidden
    @Schema(description = "대표이미지 파일명 (서버에서 자동 설정)", example = "파일명 저장용: ex) 927fa93-test.png")
    private String imageUrl;

    @NotNull
    @Schema(description = "정가", example = "20000")
    private int price;

    @Schema(description = "할인가", example = "15000")
    private int discountPrice;

    @Schema(description = "판매상태", example = "판매중")
    private SalesStatus salesStatus;

    @Schema(description = "재고수량", example = "100")
    private int stock;

    @Schema(description = "판매시작일", example = "2025-01-01")
    private LocalDate salesStartDate;

    @Schema(description = "판매종료일", example = "2025-12-31")
    private LocalDate salesEndDate;

    @Schema(description = "표시순서", example = "1")
    private int displayOrder;

    @Schema(description = "활성화여부", example = "true")
    private boolean visible;

    @Hidden
    private UUID id;
}
