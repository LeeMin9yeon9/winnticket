package kr.co.winnticket.integration.spavis.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.integration.spavis.client.SpavisClient;
import kr.co.winnticket.integration.spavis.dto.SPCouponCheckResponse;
import kr.co.winnticket.integration.spavis.service.SpavisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/spavis/test")
@RequiredArgsConstructor
@Tag(name="Spavis")
public class SpavisController {

    private final SpavisService service;

    @GetMapping("/check")
    public SPCouponCheckResponse check(@RequestParam UUID orderId) throws Exception {
        return service.check(orderId);
    }
}
