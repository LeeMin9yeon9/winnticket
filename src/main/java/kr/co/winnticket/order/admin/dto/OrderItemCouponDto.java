package kr.co.winnticket.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderItemCouponDto {
    @Schema(description = "order_item_coupons id")
    private UUID id;

    @Schema(description = "주문 id")
    private UUID orderId;

    @Schema(description = "주문 아이템 id")
    private UUID orderItemId;

    @Schema(description = "상품 id")
    private UUID productId;

    @Schema(description = "상품 옵션값 id")
    private UUID productOptionValueId;

    @Schema(description = "예약된 티켓 쿠폰 id")
    private UUID ticketCouponId;

    @Schema(description = "쿠폰 번호")
    private String couponNumber;
}
