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
    private final AquaplanetProperties props;
    private final AquaplanetResponseMapper responseMapper;

    // 상품조회
    public AquaplanetEnvelope<APContractProductResponse> contractProducts(APContractProductRequest req) {
        AquaplanetEnvelope<APContractProductResponse> res =
                client.call(props.getRecvSvcCdContract(),
                        props.getIntfIdContract(),
                        req,
                        APContractProductResponse.class);

        validate(responseMapper.map(res), "아쿠아플라넷 상품조회 실패");

        return res;
    }

    // 쿠폰발행
    public AquaplanetEnvelope<APCouponIssueResponse> couponIssue(APCouponIssueRequest req) {
        AquaplanetEnvelope<APCouponIssueResponse> res =
                client.call(props.getRecvSvcCdIssue(),
                        props.getIntfIdIssue(),
                        req,
                        APCouponIssueResponse.class);

        validate(responseMapper.map(res), "아쿠아플라넷 쿠폰발행 실패");

        return res;
    }

    // 쿠폰취소
    public AquaplanetEnvelope<APCouponCancelResponse> couponCancel(APCouponCancelRequest req) {
        AquaplanetEnvelope<APCouponCancelResponse> res =
                client.call(props.getRecvSvcCdCancel(),
                        props.getIntfIdCancel(),
                        req,
                        APCouponCancelResponse.class);

        validate(responseMapper.map(res), "아쿠아플라넷 쿠폰취소 실패");

        return res;
    }

    // 회수이력 단건조회
    public AquaplanetEnvelope<APCouponUseHistoryResponse> couponUseHistory(APCouponUseHistoryRequest req) {
        AquaplanetEnvelope<APCouponUseHistoryResponse> res =
                client.call(props.getRecvSvcCdUseHistory(),
                        props.getIntfIdUseHistory(),
                        req,
                        APCouponUseHistoryResponse.class);

        validate(responseMapper.map(res), "아쿠아플라넷 사용이력 조회 실패");

        return res;
    }

    // 회수이력 날짜별조회
    public AquaplanetEnvelope<APCouponUseDailyHistoryResponse> couponUseDailyHistory(APCouponUseDailyHistoryRequest req) {
        AquaplanetEnvelope<APCouponUseDailyHistoryResponse> res =
                client.call(props.getRecvSvcCdUseDailyHistory(),
                        props.getIntfIdUseDailyHistory(),
                        req,
                        APCouponUseDailyHistoryResponse.class);

        validate(responseMapper.map(res), "아쿠아플라넷 사용이력 일자조회 실패");

        return res;
    }

    private void validate(IntegrationResult result, String defaultMessage) {
        if (!result.isSuccess()) {
            throw new RuntimeException(
                    defaultMessage
                            + " - code: "
                            + result.getCode()
                            + ", message: "
                            + result.getMessage()
            );
        }
    }
}