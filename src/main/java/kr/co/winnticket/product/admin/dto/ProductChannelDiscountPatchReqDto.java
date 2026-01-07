package kr.co.winnticket.product.admin.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[상품 > 상품 채널별 할인 수정] ProductChannelDiscountPatchReqDto")
public class ProductChannelDiscountPatchReqDto {
    @Schema(description = "할인율")
    private int discountRate;

    @Schema(description = "시작일")
    private LocalDate startDate;

    @Schema(description = "종료일")
    private LocalDate endDate;
}
