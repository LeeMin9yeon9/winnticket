package kr.co.winnticket.integration.benepia.order.client;

import kr.co.winnticket.integration.benepia.props.BenepiaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Service
@RequiredArgsConstructor
@Log4j2
public class BenepiaOrderBatchApiClient {

    private final RestTemplate restTemplate;

    @Value("${benepia.order-base-url}")
    private String orderBaseUrl;

    @Value("${benepia.kcp-co-cd}")
    private String kcpCoCd;

    public void uploadBatch(File file) {

        String url = orderBaseUrl + "/v1/partners/" + kcpCoCd + "/orders";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, request, String.class);

        log.info("[BENEPIA BATCH UPLOAD SUCCESS] status={}, body={}",
                response.getStatusCode(), response.getBody());
    }
}