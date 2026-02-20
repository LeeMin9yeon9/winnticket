package kr.co.winnticket.ticketCoupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(title = "[상품 > 쿠폰그룹 조회 요청 DTO] TicketCouponGroupResDto")
public class TicketCouponGroupResDto {

    @Schema(description = "그룹ID")
    private UUID id;

    @Schema(description="상품ID")
    private UUID productId;

    @Schema(description="상품옵션ID")
    private UUID productOptionId;

    @Schema(description="상품옵션값ID")
    private UUID productOptionValueId;

    @Schema(description="유효시작일")
    private LocalDate validFrom;

    @Schema(description="유효종료일")
    private LocalDate validUntil;

    @Schema(description = "생성일")
    private LocalDateTime createdAt;

    @Schema(description = "수정일")
    private LocalDateTime updatedAt;

    @Schema(description = "사용가능 쿠폰 수")
    private Integer activeCount;

    @Schema(description = "사용된 쿠폰 수")
    private Integer usedCount;
}
