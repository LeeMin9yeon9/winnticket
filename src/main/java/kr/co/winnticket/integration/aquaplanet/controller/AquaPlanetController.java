package kr.co.winnticket.integration.aquaplanet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.integration.aquaplanet.service.AquaPlanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Tag(name = "AquaPlanet API", description = "아쿠아플라넷(한화) 연동 인터페이스")
@RestController
@RequestMapping("/api/aquaplanet/test")
@RequiredArgsConstructor
public class AquaPlanetController {

    private final AquaPlanetService aquaPlanetService;

    @Operation(summary = "1. 계약사 상품조회 (HBSSAMCNT0114)")
    @GetMapping("/products")
    public ResponseEntity<Map> getProducts(@RequestParam String corpCd, @RequestParam String contNo) {
        return ResponseEntity.ok(aquaPlanetService.searchProducts(corpCd, contNo));
    }

    @Operation(summary = "2. 쿠폰 발행 처리 (HBSSAMCPN0306)")
    @PostMapping("/issue/{orderId}")
    public ResponseEntity<Void> issueCoupon(@PathVariable UUID orderId) {
        aquaPlanetService.issue(orderId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "3. 쿠폰 발행 취소 (HBSSAMCPN1003)")
    @PostMapping("/cancel")
    public ResponseEntity<Map> cancelCoupon(@RequestParam String corpCd,
                                            @RequestParam String contNo,
                                            @RequestParam String couponNo) {
        return ResponseEntity.ok(aquaPlanetService.cancel(corpCd, contNo, couponNo));
    }

    @Operation(summary = "4. 개별 쿠폰 회수이력 조회 (HBSSAMCPN1100)")
    @GetMapping("/history/individual")
    public ResponseEntity<Map> getIndividualHistory(@RequestParam String couponNo) {
        return ResponseEntity.ok(aquaPlanetService.getHistory(couponNo));
    }

    @Operation(summary = "5. 영업일자별 쿠폰 회수이력 조회 (HBSSAMCPN1103)")
    @GetMapping("/history/daily")
    public ResponseEntity<Map> getDailyHistory(@RequestParam String bsnDate) {
        return ResponseEntity.ok(aquaPlanetService.getDailyHistory(bsnDate));
    }
}