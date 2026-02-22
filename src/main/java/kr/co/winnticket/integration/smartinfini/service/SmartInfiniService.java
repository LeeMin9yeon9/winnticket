package kr.co.winnticket.integration.smartinfini.service;

import kr.co.winnticket.integration.smartinfini.client.SmartInfiniClient;
import kr.co.winnticket.integration.smartinfini.dto.*;
import kr.co.winnticket.integration.smartinfini.mapper.SmartInfiniMapper;
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

    private static final DateTimeFormatter ORDER_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 주문
    public SIOrderResponse order(UUID orderId) {
        SIOrderRequest req = mapper.selectSmartinfiniOrder(orderId);
        req.setChannelCode(props.getChannelId());
        return client.order(req);
    }

    // 조회(단건 필요없을듯)
    public SISearchResponse search(SISearchRequest req) {
        SISearchResponse res = client.search(req);
        return res;
    }

    // 조회(다건)
    public SIOrderSearchResponse searchByOrderNo(UUID orderId) {
        SIOrderSearchRequest req = mapper.selectSmartinfinisearchByOrderNo(orderId);
        return client.searchByOrderNo(req);
    }

    // 취소(단건 필요없을듯)
    public SICancelResponse cancelSingle(SICancelRequest req) {
        SICancelResponse res = client.cancel(req);
        return res;
    }

    // 취소(다건)
    public SICancelListResponse cancelMulti(UUID orderId) {
        SICancelListRequest cancelReq = mapper.selectSmartinfiniCancelList(orderId);
        cancelReq.setResultDate(
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );
        return client.cancelList(cancelReq);
    }

    // 상품조회(상품 참고용)
    public List<SIProductResponse> product(SIProductRequest req) {
        return client.product(req);
    }

    // 문자 재전송
    public SIMmsResendResponse mmsResend(UUID orderId) {
        SIMmsResendRequest req = mapper.selectSmartinfiniMmsResend(orderId);
        return client.mmsResend(req);
    }

    // =========================
    // 내부 사용처리(콜백 수신) - 실제 우리 로직 연결
    // =========================
    public SIUseCallbackResponse onUseCallback(SIUseCallbackRequest req) {
        // 여기서 우리 DB 업데이트/검증/중복처리 등 수행
        // 지금은 "성공"만 반환(완성 뼈대)
        String orderNo = req.getOrderNo() != null ? req.getOrderNo() : "";
        return SIUseCallbackResponse.ok(orderNo);
    }
}