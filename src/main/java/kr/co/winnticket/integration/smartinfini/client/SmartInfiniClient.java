package kr.co.winnticket.integration.smartinfini.client;

import kr.co.winnticket.integration.smartinfini.config.SmartInfiniConfig;
import kr.co.winnticket.integration.smartinfini.dto.*;
import kr.co.winnticket.integration.smartinfini.props.SmartInfiniProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SmartInfiniClient {

    private final RestTemplate restTemplate;
    private final SmartInfiniConfig config;

    private <T> T post(String path, Object body, Class<T> clazz) {

        String url = config.getBaseUrl() + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(config.getToken());

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        ResponseEntity<T> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, clazz);

        return response.getBody();
    }

    // 주문
    public SIOrderResponse order(SIOrderRequest req) {
        return post("/order", req, SIOrderResponse.class);
    }

    // 조회
    public SISearchResponse search(SISearchRequest req) {
        return post("/inquiry", req, SISearchResponse.class);
    }

    // 조회(주문번호별)
    public SIUseSearchResponse useSearch(SIUseSearchRequest req) {
        return post("/oinquiry", req, SIUseSearchResponse.class);
    }

    // 주문취소(단건)
    public SICancelResponse cancel(SICancelRequest req) {
        return post("/cancel", req, SICancelResponse.class);
    }

    // 주문취소(다중)
    public SICancelResponse cancels(SICancelRequest req) {
        return post("/ocancel", req, SICancelResponse.class);
    }

    // 상품조회
    public SIProductResponse product(SIProductRequest req) {
        return post("/product_inquiry", req, SIProductResponse.class);
    }

    // 문자 재전송
    public SIMmsResendResponse resend(SIMmsResendRequest req) {
        return post("/resend", req, SIMmsResendResponse.class);
    }
}