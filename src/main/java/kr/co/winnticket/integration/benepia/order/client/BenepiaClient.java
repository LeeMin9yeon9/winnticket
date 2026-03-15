package kr.co.winnticket.integration.benepia.order.client;

import kr.co.winnticket.integration.benepia.order.dto.BenepiaCancelRequest;
import kr.co.winnticket.integration.benepia.order.dto.BenepiaOrderRequest;
import kr.co.winnticket.integration.benepia.props.BenepiaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Log4j2
public class BenepiaClient {

    private final RestTemplate restTemplate;
    private final BenepiaProperties props;

    public String sendOrder(BenepiaOrderRequest request){

        String url =
                props.getOrderBaseUrl()
                        + "/v1/partners/"
                        + props.getKcpCoCd()
                        + "/orders";

        log.info("[BENEPIA] ORDER REQUEST = {}", request);

        ResponseEntity<String> res =
                restTemplate.postForEntity(
                        url,
                        request,
                        String.class
                );

        log.info("[BENEPIA] ORDER RESPONSE = {}", res.getBody());

        return res.getBody();
    }

    public String cancelOrder(BenepiaCancelRequest request){

        String url =
                props.getOrderBaseUrl()
                        + "/v1/partners/"
                        + props.getKcpCoCd()
                        + "/orderCancel";

        log.info("[BENEPIA] CANCEL REQUEST = {}", request);

        ResponseEntity<String> res =
                restTemplate.postForEntity(
                        url,
                        request,
                        String.class
                );

        log.info("[BENEPIA] CANCEL RESPONSE = {}", res.getBody());

        return res.getBody();
    }
}