package kr.co.winnticket.integration.smartinfini.service;

import kr.co.winnticket.integration.smartinfini.client.SmartInfiniClient;
import kr.co.winnticket.integration.smartinfini.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SmartInfiniService {

    private final SmartInfiniClient client;

    private static final DateTimeFormatter ORDER_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 주문
    public SIOrderResponse order(SIOrderRequest req) {
        SIOrderResponse res = client.order(req);
        return res;
    }

    // 조회(단건)
    public SISearchResponse search(SISearchRequest req) {
        SISearchResponse res = client.search(req);
        return res;
    }

    // 조회(다건)
    public SIOrderSearchResponse searchByOrderNo(SIOrderSearchRequest req) {
        SIOrderSearchResponse res = client.searchByOrderNo(req);
        return res;
    }

    // 취소(단건)
    public SICancelResponse cancelSingle(SICancelRequest req) {
        SICancelResponse res = client.cancel(req);
        return res;
    }

    // 취소(다건)
    public SICancelListResponse cancelMulti(SICancelListRequest req) {
        SICancelListResponse res = client.cancelList(req);
        return res;
    }

    // 상품조회
    public List<SIProductResponse> product(SIProductRequest req) {
        return client.product(req);
    }

    // 문자 재전송
    public SIMmsResendResponse mmsResend(SIMmsResendRequest req) {
        SIMmsResendResponse res = client.mmsResend(req);
        return res;
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