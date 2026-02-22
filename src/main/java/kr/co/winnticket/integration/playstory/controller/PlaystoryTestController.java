package kr.co.winnticket.integration.playstory.controller;

import kr.co.winnticket.integration.playstory.client.PlaystoryClient;
import kr.co.winnticket.integration.playstory.dto.*;
import kr.co.winnticket.integration.playstory.service.PlaystoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/playstory/test")
@RequiredArgsConstructor
public class PlaystoryTestController {

    private final PlaystoryService service;

    @PostMapping("/order")
    public PlaystoryOrderResponse order(@RequestParam UUID orderId) {
        return service.order(orderId);
    }

    @PostMapping("/check")
    public PlaystoryCheckResponse check(@RequestParam UUID orderId) {
        return service.check(orderId);
    }

    @PostMapping("/cancel")
    public PlaystoryCheckCancelResponse cancel(@RequestParam UUID orderId) {
        return service.cancel(orderId);
    }
}