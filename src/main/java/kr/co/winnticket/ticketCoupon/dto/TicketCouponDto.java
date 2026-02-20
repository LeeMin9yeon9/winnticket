package kr.co.winnticket.ticketCoupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(title = "[상품 > 쿠폰생성  DTO] TicketCouponDto")
public class TicketCouponDto {
    private UUID id;

    private UUID groupId;

    private String couponNumber;

    private String status;

    private LocalDate validFrom;

    private LocalDate validUntil;

    private LocalDateTime usedAt;

}
