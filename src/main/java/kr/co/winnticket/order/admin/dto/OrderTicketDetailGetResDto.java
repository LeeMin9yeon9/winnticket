package kr.co.winnticket.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[주문 > 주문 티켓 상세 조회] OrderProductListGetResDto")
public class OrderTicketDetailGetResDto {
    @NotNull
    @Schema(description = "티켓id")
    private UUID ticketId;

    @Schema(description = "티켓번호")
    private String ticketNumber;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "사용여부")
    private boolean ticketUsed;

    @Schema(description = "사용일시")
    private LocalDateTime ticketUsedDate;
}
