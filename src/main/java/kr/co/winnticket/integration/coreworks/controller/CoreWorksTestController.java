package kr.co.winnticket.integration.coreworks.controller;

import kr.co.winnticket.integration.coreworks.dto.*;
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

    @PostMapping("/search")
    public CWSearchResponse search(@RequestBody CWSearchRequest req) {
        return service.testSearch(req);
    }

    @PostMapping("/cancel")
    public CWCancelResponse cancel(@RequestBody CWCancelRequest req) {
        return service.testCancel(req);
    }

    @GetMapping("/useSearch")
    public CWUseSearchResponse useSearch(
            @RequestParam String start,
            @RequestParam String end) {
        return service.testUseSearch(start, end);
    }
}
