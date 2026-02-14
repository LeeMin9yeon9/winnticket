package kr.co.winnticket.integration.aquaplanet.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.integration.aquaplanet.dto.common.AquaplanetEnvelope;
import kr.co.winnticket.integration.aquaplanet.dto.contract.APContractProductRequest;
import kr.co.winnticket.integration.aquaplanet.dto.contract.APContractProductResponse;
import kr.co.winnticket.integration.aquaplanet.dto.coupon.*;
import kr.co.winnticket.integration.aquaplanet.service.AquaplanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aquaplanet/test")
@Tag(name = "AQUAPLANET 테스트", description = "아쿠아플라넷 연동 Swagger 테스트")
public class AquaplanetTestController {

    private final AquaplanetService service;

    @PostMapping("/contract/products")
    public AquaplanetEnvelope<APContractProductResponse> contractProducts(@RequestBody APContractProductRequest req) {
        return service.contractProducts(req);
    }

    @PostMapping("/coupon/issue")
    public AquaplanetEnvelope<APCouponIssueResponse> couponIssue(@RequestBody APCouponIssueRequest req) {
        return service.couponIssue(req);
    }

    @PostMapping("/coupon/cancel")
    public AquaplanetEnvelope<APCouponCancelResponse> couponCancel(@RequestBody APCouponCancelRequest req) {
        return service.couponCancel(req);
    }

    @PostMapping("/coupon/use-history")
    public AquaplanetEnvelope<APCouponUseHistoryResponse> couponUseHistory(@RequestBody APCouponUseHistoryRequest req) {
        return service.couponUseHistory(req);
    }

    @PostMapping("/coupon/use-daily-history")
    public AquaplanetEnvelope<APCouponUseDailyHistoryResponse> couponUseDailyHistory(@RequestBody APCouponUseDailyHistoryRequest req) {
        return service.couponUseDailyHistory(req);
    }
}
