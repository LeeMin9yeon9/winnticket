package kr.co.winnticket.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.common.enums.OrderStatus;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[주문 > 주문 목록 조회] OrderAdminListGetResDto")
public class OrderAdminListGetResDto {
    @NotNull
    @Schema(description = "주문_ID")
    private UUID id;

    @Schema(description = "주문일")
    private LocalDate paymentDate;

    @Schema(description = "주문번호")
    private String orderNumber;

    @Schema(description = "주문자")
    private String customerName;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "수량")
    private int quantity;

    @Schema(description = "상품가격")
    private int unitPrice;

    @Schema(description = "주문상태")
    private OrderStatus status;

    @Schema(description = "총주문금액")
    private int totalPrice;

    @Schema(description = "결제수단")
    private String paymentMethod;

    @Schema(description = "연락처")
    private String customerPhone;
}
