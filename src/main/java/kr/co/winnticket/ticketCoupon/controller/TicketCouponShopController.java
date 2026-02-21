package kr.co.winnticket.ticketCoupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.ticketCoupon.service.TicketCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ticketCoupon")
@Tag(name = "SHOP 선사입 티켓쿠폰 발급", description = "")
public class TicketCouponShopController {

    private final TicketCouponService service;
    @Operation(summary = "쿠폰 발급", description = "구매 시 사용가능 쿠폰을 SOLD 상태로 변경하고 쿠폰번호 반환")
    @PostMapping("/issue/{orderItemId}")
    public ResponseEntity<ApiResponse<String>> issueCoupon(
            @PathVariable UUID orderItemId
    ) {

        String couponNumber = service.issueCoupon(orderItemId);

        return ResponseEntity.ok(ApiResponse.success("쿠폰 발급 성공", couponNumber)
        );
    }
}
