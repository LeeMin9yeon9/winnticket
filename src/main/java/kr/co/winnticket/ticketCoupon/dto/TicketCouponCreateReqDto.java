package kr.co.winnticket.ticketCoupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Schema(title = "[상품 > 쿠폰생성 요청 DTO] TicketCouponCreateReqDto")
public class TicketCouponCreateReqDto {
    @Schema(description="상품 ID")
    private UUID productId;

    @Schema(description="상품 옵션 ID")
    private UUID productOptionId;

    @Schema(description="상품 옵션값 ID")
    private UUID productOptionValueId;

    @Schema(description="시작 쿠폰번호")
    private String startNumber;

    @Schema(description="끝 쿠폰번호")
    private String endNumber;

    @Schema(description="사용 시작일")
    private LocalDate validFrom;

    @Schema(description="사용 종료일")
    private LocalDate validUntil;
}
