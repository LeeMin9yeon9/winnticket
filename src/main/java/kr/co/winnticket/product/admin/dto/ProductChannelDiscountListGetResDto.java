package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.common.enums.ProductType;
import kr.co.winnticket.common.enums.SalesStatus;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 채널별 할인 목록 조회] ProductChannelDiscountListGetResDto")
public class ProductChannelDiscountListGetResDto {
    @NotNull
    @Schema(description = "할인_ID")
    private UUID id;

    @Schema(description = "상품_ID")
    private UUID productId;

    @Schema(description = "채널_ID")
    private UUID channelId;

    @Schema(description = "채널명")
    private String channelName;

    @Schema(description = "할인율")
    private int discountRate;

    @Schema(description = "정가")
    private int originalPrice;

    @Schema(description = "판매가")
    private int salePrice;

    @Schema(description = "시작일")
    private LocalDate startDate;

    @Schema(description = "종료일")
    private LocalDate endDate;

    @Schema(description = "상태")
    private String status;

    @Schema(description = "기간")
    private String period;

    @Schema(description = "노출여부")
    private boolean isActive;
}
