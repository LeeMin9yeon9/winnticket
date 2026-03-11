package kr.co.winnticket.integration.aquaplanet.controller;

import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetApiResponse;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetIssueResponse;
import kr.co.winnticket.integration.aquaplanet.service.AquaPlanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aquaplanet/test")
public class AquaPlanetController {

    private final AquaPlanetService service;

    @PostMapping("/issue/{orderId}")
    public ResponseEntity<AquaPlanetApiResponse<List<AquaPlanetIssueResponse>>> issue(@PathVariable UUID orderId) {
        List<AquaPlanetIssueResponse> result = service.issueOrder(orderId);
        return ResponseEntity.ok(new AquaPlanetApiResponse<>(true, "아쿠아플라넷 발행 완료", result));
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<AquaPlanetApiResponse<Void>> cancel(@PathVariable UUID orderId) {
        service.cancelOrder(orderId);
        return ResponseEntity.ok(new AquaPlanetApiResponse<>(true, "아쿠아플라넷 취소 완료", null));
    }
}