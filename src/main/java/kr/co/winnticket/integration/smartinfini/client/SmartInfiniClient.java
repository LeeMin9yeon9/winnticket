package kr.co.winnticket.integration.smartinfini.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.smartinfini.dto.*;
import kr.co.winnticket.integration.smartinfini.props.SmartInfiniProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class SmartInfiniClient {

    private final RestTemplate smartInfiniRestTemplate;
    private final ObjectMapper smartInfiniObjectMapper;
    private final SmartInfiniProperties props;

    // 주문
    public SIOrderResponse order(SIOrderRequest req) {
        return post("/order", req, SIOrderResponse.class);
    }

    // 조회(단건)
    public SISearchResponse search(SISearchRequest req) {
        return post("/inquiry", req, SISearchResponse.class);
    }

    // 조회(다건)
    public SIOrderSearchResponse searchByOrderNo(SIOrderSearchRequest req) {
        return post("/oinquiry", req, SIOrderSearchResponse.class);
    }

    // 취소(환불)
    public SICancelResponse cancel(SICancelRequest req) {
        return post("/cancel", req, SICancelResponse.class);
    }

    // 취소(다건)
    public SICancelListResponse cancelList(SICancelListRequest req) {
        return post("/ocancel", req, SICancelListResponse.class);
    }

    // 상품조회
    public List<SIProductResponse> product(SIProductRequest req) {
        return post("/product_inquiry", req, new ParameterizedTypeReference<List<SIProductResponse>>() {});
    }

    // 문자 재전송
    public SIMmsResendResponse mmsResend(SIMmsResendRequest req) {
        return post("/resend", req, SIMmsResendResponse.class);
    }

    private <T> T post(
            String path,
            Object body,
            ParameterizedTypeReference<T> type
    ){
        String url = props.getBaseUrl() + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + props.getToken());

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        ResponseEntity<T> response =
                smartInfiniRestTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        type
                );

        return response.getBody();
    }

    private <T> T post(String path, Object body, Class<T> clazz) {
        String url = props.getBaseUrl() + path;

        try {
            String reqJson = smartInfiniObjectMapper.writeValueAsString(body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.add("Authorization", "Bearer " + props.getToken().trim()); // 표준

            HttpEntity<String> entity = new HttpEntity<>(reqJson, headers);

            ResponseEntity<String> response =
                    smartInfiniRestTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            String resBody = response.getBody();

            return smartInfiniObjectMapper.readValue(resBody, clazz);

        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}