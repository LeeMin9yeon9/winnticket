package kr.co.winnticket.integration.benepia.pointVoucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(title = "[관리자 > 이용권 사용내역] PointVoucherUsageResDto")
public class PointVoucherUsageResDto {

    @Schema(description = "사용이력 ID")
    private UUID id;

    @Schema(description = "사용된 주문번호")
    private String orderNumber;

    @Schema(description = "사용된 주문 ID (주문 상세 페이지 이동용, 주문이 삭제됐으면 null)")
    private UUID orderId;

    @Schema(description = "사용일시")
    private LocalDateTime usedAt;

    @Schema(description = "사용전금액")
    private Integer amountBefore;

    @Schema(description = "사용금액")
    private Integer usedAmount;

    @Schema(description = "사용후잔여금액")
    private Integer amountAfter;
}
