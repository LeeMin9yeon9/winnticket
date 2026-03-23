package kr.co.winnticket.order.field.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[현장관리자 > 주문 통계] FieldOrderStatusGetResDto")
public class FieldOrderStatusGetResDto {

    @Schema(description = "총 주문 건수")
    private int totalOrderCnt;

    @Schema(description = "총 수량")
    private int totalTicketCnt;

    @Schema(description = "총 판매가")
    private long totalSalesPrice;

    @Schema(description = "총 공급가")
    private long totalSupplyPrice;
}
