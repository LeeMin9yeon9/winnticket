package kr.co.winnticket.integration.woongjin.service;

import kr.co.winnticket.integration.woongjin.client.WoongjinClient;
import kr.co.winnticket.integration.woongjin.dto.*;
import kr.co.winnticket.integration.woongjin.mapper.WoongjinMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WoongjinService {

    private final WoongjinClient client;
    private final WoongjinMapper mapper;

    // 상품조회
    public WJProductListResponse fetchProducts(Integer size, Integer index) {
        return client.getProducts(
                size == null ? 1 : size,
                index == null ? 0 : index
        );
    }

    // 상품주문
    public WJOrderResponse order(UUID orderId) {
        WJOrderRequest req = mapper.selectWJOrder(orderId);
        return client.order(req);
    }

    // 주문조회 (사용안해도될거같음)
    public WJOrderInquiryResponse inquiry(String channelOrderNumber) {
        return client.inquiry(channelOrderNumber);
    }

    // 주문취소
    public WJCancelResponse cancel(UUID orderId) {
        WJCancelRequest req = mapper.selectWJCancel(orderId);
        return client.cancel(req);
    }

    // 핀번호 재전송
    public WJResendResponse resendPin(UUID orderId) {
        WJResendRequest req = mapper.selectWJResend(orderId);
        return client.resend(req);
    }
}