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
@Schema(title = "[베네피아 포인트 → 이용권 전환 응답] PointVoucherExchangeResDto")
public class PointVoucherExchangeResDto {

    @Schema(description = "이용권 번호 (16자리)")
    private String voucherNumber;

    @Schema(description = "전환된 총 금액")
    private Integer totalAmount;

    @Schema(description = "사용기한")
    private LocalDateTime validUntil;
}
