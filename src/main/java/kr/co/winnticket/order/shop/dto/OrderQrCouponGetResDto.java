package kr.co.winnticket.order.shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "[주문 > 문자 QR 조회 응답 Dto]OrderQrCouponGetResDto")
public class OrderQrCouponGetResDto {

    @Schema(description = "파트너 ID")
    private String partnerId;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "상품옵션명")
    private String optionName;

    @Schema(description = "주문자명")
    private String customerName;

    @Schema(description = "티켓수량")
    private Integer quantity;

    @Schema(description = "주문문자 발송일시")
    private String issuedAt;

    @Schema(description = "사용기한")
    private String expireDate;

    @Schema(description = "고객센터 전화번호")
    private String customerCenterPhone;

    @Schema(description = "티켓 목록")
    private List<Ticket> tickets;

    @Data
    public static class Ticket {

        @Schema(description = "티켓 ID")
        private String ticketId;

        @Schema(description = "티켓번호")
        private String ticketNumber;

        @Schema(description = "파트너 쿠폰번호")
        private String partnerOrderCode;

        @Schema(description = "QR 값")
        private String qrValue;

        @Schema(description = "사용 여부")
        private Boolean ticketUsed;

        @Schema(description = "티켓 상태")
        private String status;

    }
}
