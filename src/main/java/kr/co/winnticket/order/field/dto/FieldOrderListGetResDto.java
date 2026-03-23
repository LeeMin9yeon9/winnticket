package kr.co.winnticket.order.field.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(title = "[현장관리자 > 주문(티켓) 목록 조회] FieldOrderListGetResDto")
public class FieldOrderListGetResDto {

    @Schema(description = "티켓ID")
    private UUID ticketId;

    @Schema(description = "주문ID")
    private UUID orderId;

    @Schema(description = "주문번호")
    private String orderNumber;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "예약일자")
    private LocalDateTime orderedAt;

    @Schema(description = "판매가")
    private Integer unitPrice;

    @Schema(description = "공급가")
    private Integer supplyPrice;

    @Schema(description = "쿠폰번호")
    private String couponNumber;

    @Schema(description = "유효기간 시작")
    private String validFrom;

    @Schema(description = "유효기간 종료")
    private String validTo;

    @Schema(description = "회사/관명")
    private String partnerName;

    @Schema(description = "이름")
    private String customerName;

    @Schema(description = "휴대폰번호")
    private String customerPhone;

    @Schema(description = "발송일시")
    private LocalDateTime ticketSentDate;

    @Schema(description = "사용일시")
    private LocalDateTime ticketUsedDate;

    @Schema(description = "취소일시")
    private LocalDateTime canceledAt;

    @Schema(description = "상태")
    private String ticketStatus;

    @Schema(description = "처리일시")
    private LocalDateTime processedAt;
}
