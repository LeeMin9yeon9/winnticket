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

    @Schema(description = "수령자명 (쿠폰 수신)")
    private String recipientName;

    @Schema(description = "수령자 연락처 (쿠폰 수신, 미입력 시 주문자 연락처로 발송)")
    private String recipientPhone;

    @Email
    @Schema(description = "주문자 이메일")
    private String customerEmail;

    @Schema(description = "회사명(기관명)")
    private String companyName;

    @Schema(description = "메모")
    private String memo;

    @Min(0)
    @Schema(description = "총금액")
    private int totalPrice;

    @Min(0)
    @Schema(description = "할인금액")
    private int discountPrice;

    @Min(0)
    @Schema(description = "사용 포인트 금액", example = "0")
    private Integer pointAmount;

    @NotNull
    @Schema(description = "결제수단", example = "BANK_TRANSFER 또는 CARD")
    private PaymentMethod paymentMethod;

    // 베네피아 kcp 전용
    private String benepiaId;
    private String benepiaPwd;


    //@Schema(description = "PG 결제수단 코드(CARD일 때만 사용)", example = "creditcard")
    //private String pgCode;

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

        @Schema(description = "숙박형 선택 날짜 리스트(숙박형일 때만 사용). 체크인~체크아웃이면 프론트에서 날짜들로 풀어서 보내기")
        private List<java.time.LocalDate> stayDates;
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
