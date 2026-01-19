package kr.co.winnticket.integration.plusn.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.plusn.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlusNClient {

    private final RestTemplate plusNRestTemplate;

    @Value("${plusn.base-url}")
    private String baseUrl;

    @Value("${plusn.order-company}")
    private String orderCompany;

    // 공통 POST JSON 메서드
    private <T> T postJson(String path, Object req, Class<T> responseType) {

        String url = baseUrl + path + "/" + orderCompany;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(req, headers);

        // 먼저 String으로 받음
        ResponseEntity<String> response =
                plusNRestTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // RAW 로그 출력
        System.out.println("\n==============================");
        System.out.println("PLUSN URL = " + url);
        System.out.println("PLUSN REQUEST = " + req);
        System.out.println("PLUSN RAW RESPONSE BODY = " + response.getBody());
        System.out.println("==============================\n");

        // 직접 DTO 변환
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), responseType);
        } catch (Exception e) {
            throw new RuntimeException("PLUSN JSON PARSE ERROR", e);
        }
    }

    // 주문
    public PlusNOrderResponse order(PlusNOrderRequest req) {
        return postJson("/order2", req, PlusNOrderResponse.class);
    }

    // 취소
    public PlusNCancelResponse cancel(PlusNCancelRequest req) {
        return postJson("/cancel2", req, PlusNCancelResponse.class);
    }

    // 티켓조회
    public PlusNInquiryResponse inquiry(PlusNInquiryRequest req) {
        return postJson("/inquiry", req, PlusNInquiryResponse.class);
    }

    // 날짜별 사용조회
    public PlusNUsedDateResponse usedDate(PlusNUsedDateRequest req) {
        return postJson("/useddate", req, PlusNUsedDateResponse.class);
    }
}
