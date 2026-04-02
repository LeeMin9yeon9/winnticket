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
@Schema(title = "[상품 > 상품 기본정보 수정] ProductBasicPatchReqDto")
public class ProductBasicPatchReqDto {
    @NotEmpty
    @Schema(description = "상품명")
    private String name;

    @NotEmpty
    @Schema(description = "상품코드")
    private String code;

    @Schema(description = "상품타입")
    private ProductType type;

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

    @Schema(description = "지역코드")
    private String regionCode;

    @Schema(description = "티켓타입")
    private String ticketType;

    @Schema(description = "선사입형 여부")
    private boolean prePurchased;

    @Schema(description = "판매시작일")
    private LocalDate salesStartDate;

    @Schema(description = "판매종료일")
    private LocalDate salesEndDate;

    @Schema(description = "활성화여부")
    private boolean visible;

    @Schema(description = "예약상품여부")
    private boolean isReservation;

    @Hidden
    @Schema(description = "아이디")
    private UUID id;
}
