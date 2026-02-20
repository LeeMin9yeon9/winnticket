package kr.co.winnticket.ticketCoupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(title = "[상품 > 쿠폰조회 결과 DTO] TicketCouponListResDto")
public class TicketCouponListResDto {
    @Schema(description = "쿠폰ID")
    private UUID id;

    @Schema(description = "그룹ID")
    private UUID groupId;

    @Schema(description = "쿠폰번호")
    private String couponNumber;

    @Schema(description = "쿠폰상태")
    private String status;

    @Schema(description = "유효시작일")
    private LocalDate validFrom;

    @Schema(description = "유효종료일")
    private LocalDate validUntil;

    @Schema(description = "쿠폰 사용일자")
    private LocalDateTime usedAt;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    @Schema(description = "수정일")
    private LocalDateTime updatedAt;
}
