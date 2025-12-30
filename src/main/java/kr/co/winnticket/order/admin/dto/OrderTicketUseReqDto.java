package kr.co.winnticket.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString(callSuper = true)
@Schema(title = "[주문 > 티켓사용] OrderTicketUseReqDto")
public class OrderTicketUseReqDto {

    @NotNull
    @Schema(description = "티켓_id")
    private UUID ticketId;

    @NotNull
    @Schema(description = "관리자_id")
    private UUID adminId;
}
