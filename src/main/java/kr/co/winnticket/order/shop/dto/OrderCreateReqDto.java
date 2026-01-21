package kr.co.winnticket.order.shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import kr.co.winnticket.common.enums.PaymentMethod;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[주문 > 주문 생성 요청] OrderCreateReqDto")
public class OrderCreateReqDto {


    @NotNull
    @Schema(description = "채널ID")
    private UUID channelId;

    @NotBlank
    @Schema(description = "주문자명")
    private String customerName;

    @NotBlank
    @Schema(description = "주문자 연락처")
    private String customerPhone;

    @Email
    @Schema(description = "주문자 이메일")
    private String customerEmail;

    @Min(0)
    @Schema(description = "총금액")
    private int totalPrice;

    @Min(0)
    @Schema(description = "할인금액")
    private int discountPrice;

    @NotNull
    @Schema(description = "결제수단", example = "BANK_TRANSFER 또는 CARD")
    private PaymentMethod paymentMethod;

    @Schema(description = "PG 결제수단 코드(CARD일 때만 사용)", example = "creditcard")
    private String pgCode;

    @Valid
    @NotEmpty
    @Schema(description = "주문 상품 목록")
    private List<OrderItemReqDto> items;

    @Data
    public static class OrderItemReqDto {

        @NotNull
        @Schema(description = "상품 ID")
        private UUID productId;

        @Min(1)
        @Schema(description = "수량")
        private int quantity;

        @Min(0)
        @Schema(description = "상품가격")
        private int unitPrice;

        @Min(0)
        @Schema(description = "상품총가격")
        private int totalPrice;

        @Valid
        @Schema(description = "선택 옵션")
        private List<OrderItemOptionReqDto> options;
    }

    @Data
    public static class OrderItemOptionReqDto {
        @NotNull
        @Schema(description = "옵션ID")
        private UUID optionId;

        @NotNull
        @Schema(description = "옵션값ID")
        private UUID optionValueId;
    }
}
