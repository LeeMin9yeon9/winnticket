package kr.co.winnticket.integration.plusn.controller;

import kr.co.winnticket.integration.plusn.dto.PlusNCancelResponse;
import kr.co.winnticket.integration.plusn.dto.PlusNInquiryResponse;
import kr.co.winnticket.integration.plusn.dto.PlusNOrderResponse;
import kr.co.winnticket.integration.plusn.dto.PlusNUsedDateResponse;
import kr.co.winnticket.integration.plusn.service.PlusNService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plusn/test")
@RequiredArgsConstructor
public class PlusNTestController {

    private final PlusNService service;

    // 1) 주문
    @GetMapping("/order")
    public PlusNOrderResponse order() {
        return service.testOrder();
    }

    // 2) 티켓조회
    @GetMapping("/inquiry")
    public PlusNInquiryResponse inquiry(
            @RequestParam String orderId,
            @RequestParam String orderSales) {
        return service.testInquiry(orderId, orderSales);
    }

    // 3) 취소
    @PostMapping("/cancel")
    public PlusNCancelResponse cancel(
            @RequestParam String orderId,
            @RequestParam String orderSales) {
        return service.testCancel(orderId, orderSales);
    }

    // 4) 날짜별 사용조회
    @GetMapping("/useddate")
    public PlusNUsedDateResponse usedDate(
            @RequestParam String date) {
        return service.testUsedDate(date);
    }
}