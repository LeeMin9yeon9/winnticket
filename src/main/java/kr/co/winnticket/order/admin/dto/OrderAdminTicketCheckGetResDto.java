package kr.co.winnticket.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[주문 > 티켓체크] OrderAdminTicketCheckGetResDto")
public class OrderAdminTicketCheckGetResDto {
    @Schema(description = "주문 ID")
    private UUID orderId;

    @Schema(description = "주문번호")
    private String orderNumber;

    @Schema(description = "주문자명")
    private String customerName;

    @Schema(description = "주문자 연락처")
    private String customerPhone;

    @Schema(description = "전체 티켓 수")
    private int totalTicketCnt;

    @Schema(description = "사용 완료 티켓 수")
    private int usedTicketCnt;

    @Schema(description = "미사용 티켓 수")
    private int unusedTicketCnt;

    @Schema(description = "티켓 목록 (좌우 넘기기 대상)")
    private List<OrderTicketDetailGetResDto> tickets = new ArrayList<>();
}
