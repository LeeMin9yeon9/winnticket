package kr.co.winnticket.ticketCoupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(title = "[상품 > 쿠폰 상태 업데이트 DTO] TicketCouponUpdateDto")
public class TicketCouponUpdateReqDto {

    @Schema(description = "쿠폰번호(변경 가능)", example = "S202600000001")
    private String couponNumber;

    @Schema(description = "쿠폰 상태(변경 가능)", example = "ACTIVE")
    private String status;

    @Schema(description = "사용 처리 시간(변경 가능)", example = "2026-02-19T12:00:00")
    private LocalDateTime usedAt;

    @Schema(description = "유효시작일(변경 가능)")
    private LocalDate validFrom;

    @Schema(description = "유효종료일(변경 가능)")
    private LocalDate validUntil;
}
