package kr.co.winnticket.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.winnticket.common.enums.OrderStatus;
import kr.co.winnticket.common.enums.PaymentMethod;
import kr.co.winnticket.common.enums.PaymentStatus;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[주문 > 주문 목록 조회] OrderAdminListGetResDto")
public class OrderAdminListGetResDto {
    @NotNull
    @Schema(description = "주문_ID")
    private UUID id;

    @Schema(description = "티켓사용여부")
    private boolean allTicketUsed;

    @Schema(description = "주문일")
    private LocalDateTime orderedAt;

    @Schema(description = "주문번호")
    private String orderNumber;

    @Schema(description = "파트너명")
    private String partnerName;

    @Schema(description = "주문자")
    private String customerName;

    @Schema(description = "연락처")
    private String customerPhone;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "상품수")
    private int productCnt;

    @Schema(description = "상품총금액")
    private int totalPrice;

    @Schema(description = "주문금액")
    private int discountPrice;

    @Schema(description = "주문상태")
    private OrderStatus status;

    @Schema(description = "결제상태")
    private PaymentStatus paymentStatus;

    @Schema(description = "결제금액")
    private int finalPrice;

    @Schema(description = "결제수단")
    private PaymentMethod paymentMethod;

}
