package kr.co.winnticket.ticketCoupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(title = "[상품 > 쿠폰 사용 요청 DTO] TicketCouponUseReqDto")
public class TicketCouponUseReqDto {
    @Schema(description = "주문 ID")
    private UUID orderId;

    @Schema(description = "주문상품 ID")
    private UUID orderItemId;

    @Schema(description = "상품 ID")
    private UUID productId;

    @Schema(description = "옵션값 ID")
    private UUID optionValueId;
}
