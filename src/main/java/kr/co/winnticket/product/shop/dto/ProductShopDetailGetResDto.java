package kr.co.winnticket.product.shop.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import kr.co.winnticket.common.enums.ProductType;
import kr.co.winnticket.common.enums.SalesStatus;
import kr.co.winnticket.product.admin.dto.ProductOptionGetResDto;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 상세 조회] ProductShopDetailGetResDto")
public class ProductShopDetailGetResDto {
    @Hidden
    @Schema(description = "상품ID")
    private UUID id;

    @NotEmpty
    @Schema(description = "상품코드")
    private String code;

    @Schema(description = "상품타입")
    private ProductType type;

    @Schema(description = "상품상태")
    private SalesStatus salesStatus;

    @Schema(description = "상품이미지")
    private List<String> imageUrl;

    @Schema(description = "상품명")
    private String name;

    @Schema(description = "카테고리명")
    private String categoryName;

    @Schema(description = "상품설명")
    private String description;

    @Schema(description = "정가")
    private int price;

    @Schema(description = "판매가")
    private int discountPrice;

    @Schema(description = "할인율")
    private int discountRate;

    @Schema(description = "배송정보")
    private String shippingInfo;

    @Schema(description = "보증정보")
    private String warrantyInfo;

    @Schema(description = "반품/교환정보")
    private String returnInfo;

    @Schema(description = "상품상세설명")
    private String detailContent;

    @Schema(description = "사용기한")
    private String usagePeriod;

    @Schema(description = " 옵션")
    private List<ProductOptionGetResDto> options = new ArrayList<>();

    @Schema(description = "날짜별가격")
    private List<ProductDatePriceGetResDto> datePrices = new ArrayList<>();
}
