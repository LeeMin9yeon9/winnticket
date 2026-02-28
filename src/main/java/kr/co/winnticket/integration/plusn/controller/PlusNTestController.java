package kr.co.winnticket.integration.plusn.controller;

import io.swagger.v3.oas.annotations.Hidden;
import kr.co.winnticket.integration.plusn.dto.*;
import kr.co.winnticket.integration.plusn.service.PlusNService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/plusn/test")
@RequiredArgsConstructor
public class PlusNTestController {

    private final PlusNService service;

    // 주문
    @GetMapping("/order")
    public PlusNOrderResponse order(@RequestParam UUID orderId) {
        return service.order(orderId);
    }

    // 취소
    @PostMapping("/cancel")
    public PlusNBatchCancelResponse cancel(@RequestParam UUID orderId) {
        return service.cancel(orderId);
    }

    // 날짜별 사용조회
    @Hidden
    @GetMapping("/useddate")
    public PlusNUsedDateResponse usedDate(
            @RequestParam String date) {
        return service.usedDate(date);
    }
}