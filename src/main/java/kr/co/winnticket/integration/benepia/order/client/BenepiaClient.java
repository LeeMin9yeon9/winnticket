package kr.co.winnticket.integration.benepia.order.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.benepia.order.dto.BenepiaCancelRequest;
import kr.co.winnticket.integration.benepia.order.dto.BenepiaOrderRequest;
import kr.co.winnticket.integration.benepia.props.BenepiaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Log4j2
public class BenepiaClient {

    private final RestTemplate restTemplate;
    private final BenepiaProperties props;
    private final ObjectMapper objectMapper;

    public String sendOrder(BenepiaOrderRequest request) {
        String url = props.getOrderBaseUrl()
                + "/v1/partners/"
                + props.getKcpCoCd()
                + "/orders";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL));

        HttpEntity<BenepiaOrderRequest> entity = new HttpEntity<>(request, headers);

        log.info("[BENEPIA] ORDER URL = {}", url);
        log.info("[BENEPIA] ORDER REQUEST = {}", request);
        log.info("[BENEPIA] ORDER REQUEST JSON = {}", toJson(request));

        try {
            ResponseEntity<String> res = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("[BENEPIA] ORDER STATUS = {}", res.getStatusCode());
            log.info("[BENEPIA] ORDER RESPONSE = {}", res.getBody());
            return res.getBody();

        } catch (HttpStatusCodeException e) {
            log.error("[BENEPIA] ORDER FAIL STATUS = {}", e.getStatusCode());
            log.error("[BENEPIA] ORDER FAIL BODY = {}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            log.error("[BENEPIA] ORDER FAIL URL = {}", url, e);
            throw e;
        }
    }

    public String cancelOrder(BenepiaCancelRequest request, String orderNumber) {
        String url = props.getOrderBaseUrl()
                + "/v1/partners/"
                + props.getKcpCoCd()
                + "/orders/"
                + orderNumber
                + "/cancel";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL));

        HttpEntity<BenepiaCancelRequest> entity = new HttpEntity<>(request, headers);

        log.info("[BENEPIA] CANCEL URL = {}", url);
        log.info("[BENEPIA] CANCEL REQUEST = {}", request);
        log.info("[BENEPIA] CANCEL REQUEST JSON = {}", toJson(request));

        try {
            ResponseEntity<String> res = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            log.info("[BENEPIA] CANCEL STATUS = {}", res.getStatusCode());
            log.info("[BENEPIA] CANCEL RESPONSE = {}", res.getBody());
            return res.getBody();

        } catch (HttpStatusCodeException e) {
            log.error("[BENEPIA] CANCEL FAIL STATUS = {}", e.getStatusCode());
            log.error("[BENEPIA] CANCEL FAIL BODY = {}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            log.error("[BENEPIA] CANCEL FAIL URL = {}", url, e);
            throw e;
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "[JSON SERIALIZE FAIL] " + e.getMessage();
        }
    }
}