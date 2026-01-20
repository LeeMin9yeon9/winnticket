package kr.co.winnticket.integration.coreworks.controller;

import kr.co.winnticket.integration.coreworks.dto.CWOrderResponse;
import kr.co.winnticket.integration.coreworks.service.CoreWorksService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coreworks/test")
@RequiredArgsConstructor
public class CoreWorksTestController {

    private final CoreWorksService service;

    // Swagger / curl 테스트용
    @GetMapping("/order")
    public CWOrderResponse testOrder() {
        return service.testOrder();
    }
}
