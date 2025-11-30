package kr.co.winnticket.product.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.common.enums.SalesStatus;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 상세정보 조회] ProductDetailGetResDto")
public class ProductDetailGetResDto {
    @NotNull
    @Schema(description = "상품_ID")
    private UUID id;

    @Schema(description = "상품명")
    private String name;

    @Schema(description = "상품코드")
    private String code;

    @Schema(description = "카테고리_ID")
    private UUID categoryId;

    @Schema(description = "파트너_ID")
    private UUID partnerId;

    @Schema(description = "정가")
    private int price;

    @Schema(description = "판매가")
    private int discountPrice;

    @Schema(description = "재고")
    private int stock;

    @Schema(description = "판매상태")
    private SalesStatus salesStatus;

    @Schema(description = "상품설명")
    private String description;

    @Schema(description = "배송정보")
    private String shippingInfo;

    @Schema(description = "보증정보")
    private String warrantyInfo;

    @Schema(description = "반품/교환정보")
    private String returnInfo;

    @Schema(description = "상품상세설명")
    private String detailContent;

    @JsonIgnore
    @Schema(description = "상품상세이미지")
    private String detailImages; // DB에서 TEXT로 받는 값 (String JSON)

    @Schema(description = "상품상세이미지")
    private List<String> detailImagesList;

    @Schema(description = "섹션 관리")
    private List<ProductSectionGetResDto> sections = new ArrayList<>();

    @Schema(description = " 옵션 관리")
    private List<ProductOptionGetResDto> options = new ArrayList<>();

    @Schema(description = "sms 설정")
    private ProductSmsSettingGetResDto sms;
}