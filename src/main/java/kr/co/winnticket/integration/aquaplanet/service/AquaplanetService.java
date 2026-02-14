package kr.co.winnticket.integration.aquaplanet.service;

import kr.co.winnticket.integration.aquaplanet.client.AquaplanetClient;
import kr.co.winnticket.integration.aquaplanet.dto.common.AquaplanetEnvelope;
import kr.co.winnticket.integration.aquaplanet.dto.contract.APContractProductRequest;
import kr.co.winnticket.integration.aquaplanet.dto.contract.APContractProductResponse;
import kr.co.winnticket.integration.aquaplanet.dto.coupon.*;
import kr.co.winnticket.integration.aquaplanet.props.AquaplanetProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AquaplanetService {

    private final AquaplanetClient client;
    private final AquaplanetProperties props;

    public AquaplanetEnvelope<APContractProductResponse> contractProducts(APContractProductRequest req) {
        return client.call(props.getRecvSvcCdContract(), props.getIntfIdContract(), req, APContractProductResponse.class);
    }

    public AquaplanetEnvelope<APCouponIssueResponse> couponIssue(APCouponIssueRequest req) {
        return client.call(props.getRecvSvcCdIssue(), props.getIntfIdIssue(), req, APCouponIssueResponse.class);
    }

    public AquaplanetEnvelope<APCouponCancelResponse> couponCancel(APCouponCancelRequest req) {
        return client.call(props.getRecvSvcCdCancel(), props.getIntfIdCancel(), req, APCouponCancelResponse.class);
    }

    public AquaplanetEnvelope<APCouponUseHistoryResponse> couponUseHistory(APCouponUseHistoryRequest req) {
        return client.call(props.getRecvSvcCdUseHistory(), props.getIntfIdUseHistory(), req, APCouponUseHistoryResponse.class);
    }

    public AquaplanetEnvelope<APCouponUseDailyHistoryResponse> couponUseDailyHistory(APCouponUseDailyHistoryRequest req) {
        return client.call(props.getRecvSvcCdUseDailyHistory(), props.getIntfIdUseDailyHistory(), req, APCouponUseDailyHistoryResponse.class);
    }
}
