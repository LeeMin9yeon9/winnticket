package kr.co.winnticket.integration.benepia.pointVoucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "[관리자 > 이용권 잔여금액 변경] PointVoucherRemainingAmountUpdateReqDto")
public class PointVoucherRemainingAmountUpdateReqDto {

    @NotNull
    @Min(0)
    @Schema(description = "새 잔여금액", example = "15000")
    private Integer remainingAmount;
}
