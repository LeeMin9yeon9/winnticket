package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 채널별 할인 등록] ProductChannelDiscountPostReqDto")
public class ProductChannelDiscountPostReqDto {
    @Hidden
    @Schema(description = "할인_ID")
    private UUID id;

    @Schema(description = "채널_ID")
    private UUID channelId;

    @Schema(description = "할인율")
    private int discountRate;

    @Schema(description = "시작일")
    private LocalDate startDate;

    @Schema(description = "종료일")
    private LocalDate endDate;

    @Schema(description = "노출여부")
    private boolean isActive;
}
