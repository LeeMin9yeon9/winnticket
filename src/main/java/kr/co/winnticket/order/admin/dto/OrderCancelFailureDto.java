package kr.co.winnticket.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(title = "[주문 > 취소 실패 이력] OrderCancelFailureDto")
public class OrderCancelFailureDto {
    @Schema(description = "실패 단계 (PARTNER: 파트너 취소, PG: 결제/환불)")
    private String stage;

    @Schema(description = "실패 사유")
    private String reason;

    @Schema(description = "발생일시")
    private LocalDateTime createdAt;
}
