package kr.co.winnticket.integration.smartinfini.service;

import kr.co.winnticket.integration.smartinfini.client.SmartInfiniClient;
import kr.co.winnticket.integration.smartinfini.dto.*;
import kr.co.winnticket.integration.smartinfini.mapper.SmartInfiniMapper;
import kr.co.winnticket.integration.smartinfini.mapper.SmartInfiniResponseMapper;
import kr.co.winnticket.integration.smartinfini.props.SmartInfiniProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SmartInfiniService {

    private final SmartInfiniClient client;
    private final SmartInfiniMapper mapper;
    private final SmartInfiniProperties props;
    private final SmartInfiniResponseMapper responseMapper;

    private static final DateTimeFormatter CANCEL_DTF =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    // =========================
    // 주문
    // =========================
    public SIOrderResponse order(UUID orderId) {

        SIOrderRequest req = mapper.selectSmartinfiniOrder(orderId);
        req.setChannelCode(props.getChannelId());

        SIOrderResponse res = client.order(req);

        var result =
                responseMapper.map(res.getReturnDiv(), res.getReturnMsg());

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "SmartInfini 주문 실패: " + result.getMessage()
            );
        }

        return res;
    }

    // =========================
    // 조회(단건)
    // =========================
    public SISearchResponse search(SISearchRequest req) {

        SISearchResponse res = client.search(req);

        var result =
                responseMapper.map(res.getReturnDiv(), res.getReturnMsg());

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "SmartInfini 조회 실패: " + result.getMessage()
            );
        }

        return res;
    }

    // =========================
    // 조회(다건)
    // =========================
    public SIOrderSearchResponse searchByOrderNo(UUID orderId) {

        SIOrderSearchRequest req =
                mapper.selectSmartinfinisearchByOrderNo(orderId);

        SIOrderSearchResponse res =
                client.searchByOrderNo(req);

        var result =
                responseMapper.mapSearch(res);

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "SmartInfini 주문조회 실패: " + result.getMessage()
            );
        }

        return res;
    }

    // =========================
    // 취소(단건)
    // =========================
    public SICancelResponse cancelSingle(SICancelRequest req) {

        SICancelResponse res = client.cancel(req);

        var result =
                responseMapper.map(res.getReturnDiv(), res.getReturnMsg());

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "SmartInfini 단건취소 실패: " + result.getMessage()
            );
        }

        return res;
    }

    // =========================
    // 취소(다건)
    // =========================
    public SICancelListResponse cancelMulti(UUID orderId) {

        SICancelListRequest cancelReq =
                mapper.selectSmartinfiniCancelList(orderId);

        cancelReq.setResultDate(
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );

        SICancelListResponse res =
                client.cancelList(cancelReq);

        var result =
                responseMapper.mapCancelList(res);

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "SmartInfini 다건취소 실패: " + result.getMessage()
            );
        }

        return res;
    }

    // =========================
    // 상품조회
    // =========================
    public List<SIProductResponse> product(SIProductRequest req) {
        return client.product(req);
    }

    // =========================
    // 문자 재전송
    // =========================
    public SIMmsResendResponse mmsResend(UUID orderId) {

        SIMmsResendRequest req =
                mapper.selectSmartinfiniMmsResend(orderId);

        SIMmsResendResponse res =
                client.mmsResend(req);

        var result =
                responseMapper.map(res.getReturn_div(), res.getReturn_msg());

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "SmartInfini 문자 재전송 실패: " + result.getMessage()
            );
        }

        return res;
    }

    // =========================
    // 내부 사용 콜백 처리
    // =========================
    public SIUseCallbackResponse onUseCallback(SIUseCallbackRequest req) {

        // TODO:
        // 1. 중복 사용 체크
        // 2. 주문 상태 업데이트
        // 3. 사용 로그 기록

        String orderNo =
                req.getOrderNo() != null ? req.getOrderNo() : "";

        return SIUseCallbackResponse.ok(orderNo);
    }
}