package kr.co.winnticket.integration.coreworks.client;

import kr.co.winnticket.integration.coreworks.dto.CWOrderRequest;
import kr.co.winnticket.integration.coreworks.dto.CWOrderResponse;
import kr.co.winnticket.integration.coreworks.props.CoreWorksProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CoreWorksClient {

    private final RestTemplate coreWorksRestTemplate;
    private final CoreWorksProperties props;

    private <T> T post(String path, Object body, Class<T> type) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", props.getAuthToken());

        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        ResponseEntity<T> res = coreWorksRestTemplate.exchange(
                props.getBaseUrl() + path,
                HttpMethod.POST,
                entity,
                type
        );

        return res.getBody();
    }

    // 주문 등록
    public CWOrderResponse order(CWOrderRequest req) {
        return post("/channel/general/v11/order", req, CWOrderResponse.class);
    }
}
