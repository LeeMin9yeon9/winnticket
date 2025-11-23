package kr.co.winnticket.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@Schema(title = "[주문 > 주문상태별 카운트 조회] OrderStatusCountGetResDto")
public class OrderStatusCountGetResDto {
    @Schema(description = "입금전")
    private int unpaidCnt;

    @Schema(description = "입금전_총액")
    private int unpaidTotalPrice;

    @Schema(description = "주문처리완료")
    private int completedCnt;

    @Schema(description = "주문처리완료_총액")
    private int completedTotalPrice;

    @Schema(description = "취소/환불")
    private int canceledCnt;

    @Schema(description = "취소/환불_총액")
    private int canceledTotalPrice;
}
