package kr.co.winnticket.integration.lscompany.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.lscompany.props.LsCompanyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class LsCompanyClient {

    private final LsCompanyProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public <T> T post(String path, Object body, Class<T> responseType) {

        try {

            String url = properties.getBaseUrl() + "/" + path;

            String jsonBody = objectMapper.writeValueAsString(body);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getToken());


            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);


            log.info("LS URL = {}", url);
            log.info("TOKEN = {}", properties.getToken());
            log.info("REQUEST JSON = {}", jsonBody);

            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    responseType
            );
            log.info("HEADERS = {}", headers);
            log.info("LS RESPONSE STATUS = {}", response.getStatusCode());
            log.info("LS RESPONSE BODY = {}", response.getBody());

            return response.getBody();

        } catch (Exception e) {

            log.error("LS Company API 호출 실패", e);

            throw new RuntimeException("LS Company API 호출 실패", e);
        }
    }


}
