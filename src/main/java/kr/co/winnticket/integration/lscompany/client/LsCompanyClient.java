package kr.co.winnticket.integration.lscompany.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.lscompany.dto.*;
import kr.co.winnticket.integration.lscompany.props.LsCompanyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class LsCompanyClient {

    private final RestTemplate restTemplate;
    private final LsCompanyProperties properties;
    private final ObjectMapper objectMapper;

    public <T> T post(String path, Object requestBody, Class<T> responseType) {
        try {
            String url = properties.getBaseUrl() + "/" + path;
            String json = objectMapper.writeValueAsString(requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("Authorization", properties.getToken());

            HttpEntity<String> entity = new HttpEntity<>(json, headers);

            log.info("LS URL = {}", url);
            log.info("LS REQUEST JSON = {}", json);

            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    responseType
            );

            log.info("LS RESPONSE = {}", response.getBody());

            return response.getBody();
        } catch (Exception e) {
            log.error("LS API 호출 실패", e);
            throw new RuntimeException("LS API 호출 실패", e);
        }
    }

    // 시설 조회
    public LsPlaceResDto place(LsPlaceReqDto req){
        return post("place", req, LsPlaceResDto.class);
    }

    // 상품 조회
    public LsProductResDto product(LsProductReqDto req){
        return post("product", req, LsProductResDto.class);
    }

    // 티켓 발권
    public LsIssueResDto issue(LsIssueReqDto req){
        return post("issue", req, LsIssueResDto.class);
    }

}



