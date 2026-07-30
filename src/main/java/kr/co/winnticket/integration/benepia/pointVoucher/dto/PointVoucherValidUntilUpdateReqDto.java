package kr.co.winnticket.integration.benepia.pointVoucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(title = "[관리자 > 이용권 사용기한 변경] PointVoucherValidUntilUpdateReqDto")
public class PointVoucherValidUntilUpdateReqDto {

    @NotNull
    @Schema(description = "새 사용기한 (해당 날짜 23:59:59까지)", example = "2026-12-31")
    private LocalDate validUntil;
}
