package kr.co.winnticket.order.shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.common.enums.OrderStatus;
import kr.co.winnticket.common.enums.PaymentMethod;
import kr.co.winnticket.common.enums.PaymentStatus;
import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.order.admin.dto.OrderTicketListGetResDto;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[주문 > 주문 조회] OrderShopGetResDto")
public class OrderShopGetResDto {
    @NotNull
    @Schema(description = "주문_ID")
    private UUID id;

    @Schema(description = "주문번호")
    private String orderNumber;

    @Schema(description = "주문일시")
    private LocalDateTime orderedAt;

    @Schema(description = "채널명")
    private String channelName;

    @Schema(description = "주문상태")
    private OrderStatus status;

    @Schema(description = "주문자")
    private String customerName;

    @Schema(description = "주문자연락처")
    private String customerPhone;

    @Schema(description = "주문자이메일")
    private String customerEmail;

    @Schema(description = "결제상태")
    private PaymentStatus paymentStatus;

    @Schema(description = "결제금액")
    private int finalPrice;

    @Schema(description = "결제수단")
    private PaymentMethod paymentMethod;

    @Schema(description = "결제일시")
    private LocalDateTime paidAt;

    @Schema(description = "상품총금액")
    private int totalPrice;

    @Schema(description = "요청사항")
    private String memo;

    @Schema(description = "주문상품")
    private List<OrderProductListGetResDto> products = new ArrayList<>();
}
