package kr.co.winnticket.integration.smartinfini.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.winnticket.integration.smartinfini.dto.SIOrderRequest;
import kr.co.winnticket.integration.smartinfini.dto.SIOrderResponse;
import kr.co.winnticket.integration.smartinfini.service.SmartInfiniService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/smartInfini/test")
@RequiredArgsConstructor
public class SmartInfiniTestController {

    private final SmartInfiniService service;

    // Swagger / curl 테스트용
    @PostMapping("/order")
    public SIOrderResponse order(@RequestBody SIOrderRequest req) {
        return service.order(req);
    }
}
