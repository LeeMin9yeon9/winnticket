package kr.co.winnticket.integration.woongjin.controller;

import kr.co.winnticket.integration.woongjin.dto.*;
import kr.co.winnticket.integration.woongjin.service.WoongjinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/woongjin/test")
@RequiredArgsConstructor
public class WoongjinTestController {

    private final WoongjinService service;

    // 상품조회
    @GetMapping("/products")
    public WJProductListResponse products(
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Integer index
    ) {
        return service.fetchProducts(size, index);
    }

    // 상품주문
    @PostMapping("/order")
    public WJOrderResponse order(@RequestBody WJOrderRequest req) {
        return service.order(req);
    }

    // 주문조회
    @GetMapping("/order/inquiry")
    public WJOrderInquiryResponse inquiry(
            @RequestParam String channel_order_number
    ) {
        return service.inquiry(channel_order_number);
    }

    // 주문취소
    @PostMapping("/order/cancel")
    public WJCancelResponse cancel(@RequestBody WJCancelRequest req) {
        return service.cancel(req);
    }

    // 핀번호 재전송
    @PostMapping("/order/resend-pin")
    public WJResendResponse resendPin(@RequestBody WJResendRequest req) {
        return service.resendPin(req);
    }
}
