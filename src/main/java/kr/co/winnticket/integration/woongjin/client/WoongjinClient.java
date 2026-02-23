package kr.co.winnticket.integration.woongjin.client;

import kr.co.winnticket.integration.woongjin.dto.*;
import kr.co.winnticket.integration.woongjin.props.WoongjinProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class WoongjinClient {

    private final RestTemplate woongjinRestTemplate;
    private final WoongjinProperties props;

    private <T> T call(String url, HttpMethod method, Object body, Class<T> clazz) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + props.getApiToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(MediaType.parseMediaTypes("application/json"));

        HttpEntity<?> entity =
                (body == null)
                        ? new HttpEntity<>(headers)
                        : new HttpEntity<>(body, headers);

        ResponseEntity<T> res =
                woongjinRestTemplate.exchange(url, method, entity, clazz);

        return res.getBody();
    }

    // 상품 리스트 조회
    public WJProductListResponse getProducts(int size, int index) {
        String url = props.getBaseUrl()
                + "/channel/contents?size=" + size + "&index=" + index;
        return call(url, HttpMethod.GET, null, WJProductListResponse.class);
    }

    // 주문 요청
    public WJOrderResponse order(WJOrderRequest req) {
        String url = props.getBaseUrl() + "/channel/reservation/order";
        return call(url, HttpMethod.POST, req, WJOrderResponse.class);
    }

    // 주문 조회
    public WJOrderInquiryResponse inquiry(String channelOrderNumber) {
        String url = props.getBaseUrl() + "/channel/reservation/search?channel_order_number=" + channelOrderNumber;
        return call(url, HttpMethod.GET, null, WJOrderInquiryResponse.class);
    }

    // 주문 취소
    public WJCancelResponse cancel(WJCancelRequest req) {
        String url = props.getBaseUrl() + "/channel/reservation/cancel";
        return call(url, HttpMethod.POST, req, WJCancelResponse.class);
    }

    // PIN 재전송
    public WJResendResponse resend(WJResendRequest req) {
        String url = props.getBaseUrl() + "/channel/reservation/resend";
        return call(url, HttpMethod.POST, req, WJResendResponse.class);
    }
}