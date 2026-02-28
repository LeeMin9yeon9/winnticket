package kr.co.winnticket.integration.woongjin.service;

import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.integration.woongjin.client.WoongjinClient;
import kr.co.winnticket.integration.woongjin.dto.*;
import kr.co.winnticket.integration.woongjin.mapper.WoongjinMapper;
import kr.co.winnticket.integration.woongjin.mapper.WoongjinResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WoongjinService {

    private final WoongjinClient client;
    private final WoongjinMapper mapper;
    private final WoongjinResponseMapper responseMapper;

    // 상품조회
    public WJProductListResponse fetchProducts(Integer size, Integer index) {
        return client.getProducts(
                size == null ? 1 : size,
                index == null ? 0 : index
        );
    }

    // 상품주문
    public ApiResponse<WJOrderResponse> order(UUID orderId) {
        WJOrderRequest req = mapper.selectWJOrder(orderId);
        return responseMapper.mapOrder(client.order(req));
    }

    // 주문조회
    public ApiResponse<WJOrderInquiryResponse> inquiry(String channelOrderNumber) {
        return responseMapper.mapInquiry(client.inquiry(channelOrderNumber));
    }

    // 주문취소
    public ApiResponse<WJCancelResponse> cancel(UUID orderId) {
        WJCancelRequest req = mapper.selectWJCancel(orderId);
        return responseMapper.mapCancel(client.cancel(req));
    }

    // 핀번호 재전송
    public ApiResponse<WJResendResponse> resendPin(UUID orderId) {
        WJResendRequest req = mapper.selectWJResend(orderId);
        return responseMapper.mapResend(client.resend(req));
    }
}