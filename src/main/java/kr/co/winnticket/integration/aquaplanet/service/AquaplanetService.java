package kr.co.winnticket.integration.aquaplanet.service;

import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.aquaplanet.client.AquaplanetClient;
import kr.co.winnticket.integration.aquaplanet.dto.common.AquaplanetEnvelope;
import kr.co.winnticket.integration.aquaplanet.dto.contract.*;
import kr.co.winnticket.integration.aquaplanet.dto.coupon.*;
import kr.co.winnticket.integration.aquaplanet.mapper.AquaplanetResponseMapper;
import kr.co.winnticket.integration.aquaplanet.props.AquaplanetProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AquaplanetService {

    private final AquaplanetClient client;

    // 1) 계약사 상품조회
    public AquaplanetEnvelope<APContractProductResponse> contractProducts(APContractProductRequest req) {
        return client.call(
                "HBSSAMCNT0114",
                "SIF00HBSSAMCNT0114",
                req,
                APContractProductResponse.class
        );
    }

    // 2) 쿠폰 발행
    public AquaplanetEnvelope<APCouponIssueResponse> couponIssue(APCouponIssueRequest req) {
        return client.call(
                "HBSSAMCPN0306",
                "SIF00HBSSAMCPN0306",
                req,
                APCouponIssueResponse.class
        );
    }

    // 3) 쿠폰 발행취소
    public AquaplanetEnvelope<APCouponCancelResponse> couponCancel(APCouponCancelRequest req) {
        return client.call(
                "HBSSAMCPN1003",
                "SIF00HBSSAMCPN1003",
                req,
                APCouponCancelResponse.class
        );
    }

    // 4) 대매점 회수이력 건별 조회
    public AquaplanetEnvelope<APCouponUseHistoryResponse> couponUseHistory(APCouponUseHistoryRequest req) {
        return client.call(
                "HBSSAMCPN1100",
                "SIF00HBSSAMCPN1100",
                req,
                APCouponUseHistoryResponse.class
        );
    }

    // 5) 대매점 영업일자별 회수이력 조회
    public AquaplanetEnvelope<APCouponUseDailyHistoryResponse> couponUseDailyHistory(APCouponUseDailyHistoryRequest req) {
        return client.call(
                "HBSSAMCPN1103",
                "SIF00HBSSAMCPN1103",
                req,
                APCouponUseDailyHistoryResponse.class
        );
    }
}