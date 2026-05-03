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

        // 웅진 API 호출
        WJOrderResponse rawResponse = client.order(req);

        // 공통 응답 포맷으로 매핑 (성능 및 유효성 체크)
        ApiResponse<WJOrderResponse> response = responseMapper.mapOrder(rawResponse);

        // 성공 시 핀번호(pin) 업데이트 로직
        if (response.isSuccess() && rawResponse.getData() != null) {
            // 이중 리스트 구조 (DataBlock -> Product) 순회
            for (WJOrderResponse.DataBlock block : rawResponse.getData()) {
                if (block.getProducts() == null) continue;

                for (WJOrderResponse.Product product : block.getProducts()) {
                    String ticketNumber = product.getProduct_channel_order_number();
                    String pin = product.getPin();

                    // 티켓번호와 핀번호가 모두 있을 때만 업데이트
                    if (ticketNumber != null && pin != null) {
                        mapper.updateWJPartnerCode(ticketNumber, pin);
                    }
                }
            }
        }

        return response;
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