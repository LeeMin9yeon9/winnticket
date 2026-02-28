package kr.co.winnticket.integration.coreworks.controller;

import kr.co.winnticket.integration.coreworks.dto.*;
import kr.co.winnticket.integration.coreworks.service.CoreWorksService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/coreworks/test")
@RequiredArgsConstructor
public class CoreWorksTestController {

    private final CoreWorksService service;

    // Swagger / curl 테스트용
    @GetMapping("/order")
    public CWOrderResponse testOrder(@RequestParam UUID orderId) {
        return service.order(orderId);
    }

    @PostMapping("/search")
    public CWSearchResponse search(@RequestParam UUID orderId) {
        return service.search(orderId);
    }

    @PostMapping("/cancel")
    public CWCancelResponse cancel(@RequestParam UUID orderId) {
        return service.cancel(orderId);
    }

    @GetMapping("/useSearch")
    public CWUseSearchResponse useSearch(
            @RequestParam String start,
            @RequestParam String end) {
        return service.useSearch(start, end);
    }

    @PostMapping("/mmsResend")
    public CWMmsResendResponse mmsResend(@RequestParam UUID orderId) {
        return service.mmsResend(orderId);
    }
}
