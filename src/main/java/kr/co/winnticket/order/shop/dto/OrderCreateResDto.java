package kr.co.winnticket.order.shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[주문 > 주문 생성 응답] OrderCreateResDto")
public class OrderCreateResDto {

    @Schema(description = "주문 ID")
    private UUID orderId;

    @Schema(description = "주문번호")
    private String orderNumber;

    @Schema(description = "결제상태")
    private String paymentStatus;

    @Schema(description = "최종 결제금액")
    private int finalPrice;

    // 베네피아(Payletter 카드결제)일 때만 내려줌
    @Schema(description = "PG 제공자(PAYLETTER)", nullable = true)
    private String pgProvider;

    @Schema(description = "PG 거래키(token)", nullable = true)
    private String pgTid;

    @Schema(description = "PC 결제 URL", nullable = true)
    private String pgOnlineUrl;

    @Schema(description = "모바일 결제 URL", nullable = true)
    private String pgMobileUrl;
}
