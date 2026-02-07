package kr.co.winnticket.integration.playstory.controller;

import kr.co.winnticket.integration.playstory.client.PlaystoryClient;
import kr.co.winnticket.integration.playstory.dto.*;
import kr.co.winnticket.integration.playstory.service.PlaystoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/playstory/test")
@RequiredArgsConstructor
public class PlaystoryTestController {

    private final PlaystoryService service;

    @PostMapping("/order")
    public PlaystoryOrderResponse order(@RequestBody PlaystoryOrderRequest req) {
        return service.order(req);
    }

    @PostMapping("/check")
    public PlaystoryCheckResponse check(@RequestBody PlaystoryCheckRequest req) {
        return service.check(req);
    }

    @PostMapping("/cancel")
    public PlaystoryCheckCancelResponse cancel(@RequestBody PlaystoryCheckCancelRequest req) {
        return service.cancel(req);
    }
}