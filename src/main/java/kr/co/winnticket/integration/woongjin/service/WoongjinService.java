package kr.co.winnticket.integration.woongjin.service;

import kr.co.winnticket.integration.woongjin.client.WoongjinClient;
import kr.co.winnticket.integration.woongjin.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WoongjinService {

    private final WoongjinClient client;

    // 상품조회
    public WJProductListResponse fetchProducts(Integer size, Integer index) {
        return client.getProducts(
                size == null ? 1 : size,
                index == null ? 0 : index
        );
    }

    // 상품주문
    public WJOrderResponse order(WJOrderRequest req) {
        return client.order(req);
    }

    // 주문조회
    public WJOrderInquiryResponse inquiry(String channelOrderNumber) {
        return client.inquiry(channelOrderNumber);
    }

    // 주문취소
    public WJCancelResponse cancel(WJCancelRequest req) {
        return client.cancel(req);
    }

    // 핀번호 재전송
    public WJResendResponse resendPin(WJResendRequest req) {
        return client.resend(req);
    }
}