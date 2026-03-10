package kr.co.winnticket.integration.aquaplanet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.integration.aquaplanet.service.AquaPlanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Tag(name = "AquaPlanet API", description = "아쿠아플라넷(한화) 연동 인터페이스")
@RestController
@RequestMapping("/api/integration/aquaplanet")
@RequiredArgsConstructor
public class AquaPlanetController {

    private final AquaPlanetService aquaPlanetService;

    @Operation(summary = "1. 계약사 상품조회 (HBSSAMCNT0114)")
    @PostMapping("/products")
    public ResponseEntity<Map> getProducts(@RequestBody Map<String, String> params) {
        return ResponseEntity.ok(aquaPlanetService.getProducts(params.get("corpCd"), params.get("contNo")));
    }

    @Operation(summary = "2. 쿠폰 발행 처리 (HBSSAMCPN0306)")
    @PostMapping("/issue/{orderId}")
    public ResponseEntity<Void> issueCoupon(@PathVariable UUID orderId) {
        aquaPlanetService.issueCoupon(orderId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "3. 쿠폰 발행 취소 (HBSSAMCPN1003)")
    @PostMapping("/cancel")
    public ResponseEntity<Map> cancelCoupon(@RequestBody Map<String, String> params) {
        return ResponseEntity.ok(aquaPlanetService.cancelCoupon(params.get("corpCd"), params.get("contNo"), params.get("couponNo")));
    }

    @Operation(summary = "5. 영업일자별 쿠폰 회수이력 조회 (HBSSAMCPN1103)")
    @PostMapping("/history/daily")
    public ResponseEntity<Map> getDailyHistory(@RequestBody Map<String, String> params) {
        return ResponseEntity.ok(aquaPlanetService.getDailyHistory(params.get("bsnDate")));
    }
}