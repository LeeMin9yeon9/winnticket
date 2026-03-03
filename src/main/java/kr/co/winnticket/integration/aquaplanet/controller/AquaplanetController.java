package kr.co.winnticket.integration.aquaplanet.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.integration.aquaplanet.dto.common.AquaplanetEnvelope;
import kr.co.winnticket.integration.aquaplanet.dto.contract.APContractProductRequest;
import kr.co.winnticket.integration.aquaplanet.dto.contract.APContractProductResponse;
import kr.co.winnticket.integration.aquaplanet.dto.coupon.*;
import kr.co.winnticket.integration.aquaplanet.service.AquaplanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aquaplanet/test")
public class AquaplanetController {

    private final AquaplanetService aquaplanetService;

    /**
     * 계약사 상품조회
     */
    @PostMapping("/contract-products")
    public ResponseEntity<AquaplanetEnvelope<APContractProductResponse>> contractProducts(
            @RequestBody APContractProductRequest request
    ) {
        return ResponseEntity.ok(
                aquaplanetService.contractProducts(request)
        );
    }

    /**
     * 쿠폰 발행
     */
    @PostMapping("/coupon/issue")
    public ResponseEntity<AquaplanetEnvelope<APCouponIssueResponse>> couponIssue(
            @RequestBody APCouponIssueRequest request
    ) {
        return ResponseEntity.ok(
                aquaplanetService.couponIssue(request)
        );
    }

    /**
     * 쿠폰 발행취소
     */
    @PostMapping("/coupon/cancel")
    public ResponseEntity<AquaplanetEnvelope<APCouponCancelResponse>> couponCancel(
            @RequestBody APCouponCancelRequest request
    ) {
        return ResponseEntity.ok(
                aquaplanetService.couponCancel(request)
        );
    }

    /**
     * 대매점 회수이력 건별 조회
     */
    @PostMapping("/coupon/use-history")
    public ResponseEntity<AquaplanetEnvelope<APCouponUseHistoryResponse>> couponUseHistory(
            @RequestBody APCouponUseHistoryRequest request
    ) {
        return ResponseEntity.ok(
                aquaplanetService.couponUseHistory(request)
        );
    }

    /**
     * 대매점 영업일자별 회수이력 조회
     */
    @PostMapping("/coupon/use-history/daily")
    public ResponseEntity<AquaplanetEnvelope<APCouponUseDailyHistoryResponse>> couponUseDailyHistory(
            @RequestBody APCouponUseDailyHistoryRequest request
    ) {
        return ResponseEntity.ok(
                aquaplanetService.couponUseDailyHistory(request)
        );
    }
}
