package kr.co.winnticket.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@Schema(title = "[주문 > 주문 티켓별 기간조회] OrderTicketPeriod")
public class OrderTicketPeriod {
    @Schema(description = "유효기간_시작일자")
    private LocalDate validFrom;

    @Schema(description = "유효기간_종료일자")
    private LocalDate validTo;
}
