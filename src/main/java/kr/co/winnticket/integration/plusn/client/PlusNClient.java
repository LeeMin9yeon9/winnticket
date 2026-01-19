package kr.co.winnticket.integration.plusn.client;

import kr.co.winnticket.integration.plusn.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PlusNClient {

    private final RestTemplate plusNRestTemplate;

    @Value("${plusn.base-url}")
    private String baseUrl;

    @Value("${plusn.order-company}")
    private String orderCompany;

    // 주문
    public PlusNOrderResponse order(PlusNOrderRequest req) {
        String url = baseUrl + "/order2/" + orderCompany;
        return plusNRestTemplate.postForObject(url, req, PlusNOrderResponse.class);
    }

    // 취소
    public PlusNCancelResponse cancel(PlusNCancelRequest req) {
        String url = baseUrl + "/cancel2/" + orderCompany;
        return plusNRestTemplate.postForObject(url, req, PlusNCancelResponse.class);
    }

    // 티켓조회
    public PlusNInquiryResponse inquiry(PlusNInquiryRequest req) {
        String url = baseUrl + "/inquiry/" + orderCompany;
        return plusNRestTemplate.postForObject(url, req, PlusNInquiryResponse.class);
    }

    // 날짜별 사용조회
    public PlusNUsedDateResponse usedDate(PlusNUsedDateRequest req) {
        String url = baseUrl + "/useddate/" + orderCompany;
        return plusNRestTemplate.postForObject(url, req, PlusNUsedDateResponse.class);
    }
}
