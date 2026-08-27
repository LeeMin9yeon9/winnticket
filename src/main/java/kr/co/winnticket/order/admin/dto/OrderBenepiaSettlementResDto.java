package kr.co.winnticket.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

/**
 * 베네피아 정산용 엑셀 내보내기 - 주문상품(아이템) 단위 상세 행 DTO
 * 마감일자(결제완료일, 취소된 주문이면 취소일) 기준으로 조회됨.
 */
@Data
@Schema(title = "[주문 > 베네피아 정산] OrderBenepiaSettlementResDto")
public class OrderBenepiaSettlementResDto {

    @Schema(description = "주문 ID")
    private UUID orderId;

    @Schema(description = "주문일시")
    private String orderedAt;

    @Schema(description = "마감일자 - 결제완료일, 취소된 주문이면 취소일")
    private String closingDate;

    @Schema(description = "주문번호")
    private String orderNumber;

    @Schema(description = "주문자 이름")
    private String customerName;

    @Schema(description = "결제수단")
    private String paymentMethod;

    @Schema(description = "주문상태")
    private String status;

    @Schema(description = "주문 전체 결제금액")
    private Integer finalPrice;

    @Schema(description = "주문 전체 무통장 결제금액")
    private Integer bankAmount;

    @Schema(description = "주문 전체 카드 결제금액")
    private Integer cardAmount;

    @Schema(description = "주문 전체 포인트 결제금액")
    private Integer pointAmount;

    @Schema(description = "주문 전체 이용권 결제금액")
    private Integer voucherAmount;

    @Schema(description = "취소(환불) 금액 - 취소된 주문이면 음수, 아니면 0")
    private Integer cancelAmount;

    @Schema(description = "취소 수수료 - 취소된 주문이면 음수, 아니면 0")
    private Integer cancelFee;

    @Schema(description = "소속사코드")
    private String siteCode;

    @Schema(description = "상품명(옵션 포함)")
    private String productDisplayName;

    @Schema(description = "상품 카테고리명")
    private String categoryName;

    @Schema(description = "단가")
    private Integer unitPrice;

    @Schema(description = "수량 - 취소된 주문이면 음수")
    private Integer quantity;

    @Schema(description = "결제일시")
    private String paidAt;

    @Schema(description = "취소요청일시")
    private String cancelRequestedAt;

    @Schema(description = "취소완료일시")
    private String canceledAt;
}
