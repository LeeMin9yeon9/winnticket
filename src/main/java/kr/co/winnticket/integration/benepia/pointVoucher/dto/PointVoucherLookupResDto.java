package kr.co.winnticket.integration.benepia.pointVoucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "[이용권 조회 응답] PointVoucherLookupResDto")
public class PointVoucherLookupResDto {

    @Schema(description = "이용권 번호")
    private String voucherNumber;

    @Schema(description = "총금액")
    private Integer totalAmount;

    @Schema(description = "사용금액")
    private Integer usedAmount;

    @Schema(description = "잔여금액 (사용 가능 금액)")
    private Integer remainingAmount;

    @Schema(description = "상태 (ACTIVE/USED/CANCELLED 등)")
    private String status;

    @Schema(description = "사용기한")
    private LocalDateTime validUntil;
}
