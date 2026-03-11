package kr.co.winnticket.integration.aquaplanet.controller;

import kr.co.winnticket.integration.aquaplanet.dto.*;
import kr.co.winnticket.integration.aquaplanet.service.AquaPlanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aquaplanet/test")
public class AquaPlanetController {

    private final AquaPlanetService service;

    @PostMapping("/products")
    public String products(@RequestBody AquaPlanetProductRequest req){
        return service.searchProducts(req);
    }

    @PostMapping("/issue")
    public String issue(@RequestBody AquaPlanetIssueRequest req){
        return service.issueCoupon(req);
    }

    @PostMapping("/cancel")
    public String cancel(@RequestBody AquaPlanetCancelRequest req){
        return service.cancelCoupon(req);
    }

}