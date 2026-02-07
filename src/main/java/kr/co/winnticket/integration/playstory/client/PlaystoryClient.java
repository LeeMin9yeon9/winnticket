package kr.co.winnticket.integration.playstory.client;

import kr.co.winnticket.integration.playstory.config.PlaystoryConfig;
import kr.co.winnticket.integration.playstory.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlaystoryClient {

    private final RestTemplate restTemplate;
    private final PlaystoryConfig config;

    private <T> T post(String path, Object body, Class<T> clazz) {
        String url = config.getBaseUrl() + path;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        ResponseEntity<T> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, clazz);

        return response.getBody();
    }

    public PlaystoryOrderResponse order(PlaystoryOrderRequest req) {
        req.setChnId(config.getChnId());
        return post("/api/order", req, PlaystoryOrderResponse.class);
    }

    public PlaystoryCheckResponse check(PlaystoryCheckRequest req) {
        req.setChnId(config.getChnId());
        return post("/api/check", req, PlaystoryCheckResponse.class);
    }

    public PlaystoryCheckCancelResponse cancel(PlaystoryCheckCancelRequest req) {
        req.setChnId(config.getChnId());
        return post("/api/cancel", req, PlaystoryCheckCancelResponse.class);
    }
}
