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
    @Schema(description = "상품명")
    private String name;

    @NotEmpty
    @Schema(description = "상품코드")
    private String code;

    @NotNull
    @Schema(description = "카테고리_ID")
    private UUID categoryId;

    @Schema(description = "파트너_ID")
    private UUID partnerId;

    @Schema(description = "상품설명")
    private String description;

    @Schema(description = "대표이미지url")
    private String imageUrl;

    @NotNull
    @Schema(description = "정가")
    private int price;

    @Schema(description = "할인가")
    private int discountPrice;

    @Schema(description = "판매상태")
    private SalesStatus salesStatus;

    @Schema(description = "재고수량")
    private int stock;

    @Schema(description = "판매시작일")
    private LocalDate salesStartDate;

    @Schema(description = "판매종료일")
    private LocalDate salesEndDate;

    @Schema(description = "표시순서")
    private int displayOrder;

    @Schema(description = "활성화여부")
    private boolean visible;

    @Hidden
    @Schema(description = "아이디")
    private UUID id;
}
