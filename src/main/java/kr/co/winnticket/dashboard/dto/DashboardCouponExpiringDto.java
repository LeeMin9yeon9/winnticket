package kr.co.winnticket.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(title = "[대시보드 > 유효기간 임박 티켓 Dto] DashboardCouponExpiringDto")
public class DashboardCouponExpiringDto {

    @Schema(description = "상품 ID")
    private String productId;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "옵션명")
    private String optionName;

    @Schema(description = "쿠폰 그룹 ID")
    private String groupId;

    @Schema(description = "유효종료일")
    private LocalDate validUntil;

    @Schema(description = "남은 일수 (0이면 오늘까지)")
    private int daysLeft;

    @Schema(description = "아직 판매되지 않은 사용가능 쿠폰 수")
    private int activeCount;
}
