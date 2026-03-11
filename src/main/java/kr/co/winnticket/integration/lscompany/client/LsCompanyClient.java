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

    public LsPlaceResDto getPlaces() {

        String url = properties.getBaseUrl() + "/place";

        // 요청 DTO 생성
        LsPlaceReqDto req = new LsPlaceReqDto();
        LsPlaceReqDto.Data data = new LsPlaceReqDto.Data();
        data.setAgentNo(properties.getAgentNo());
        req.setData(data);



        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", java.nio.charset.StandardCharsets.UTF_8));
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", properties.getToken());

        log.info("LS TOKEN = {}", properties.getToken());

        ObjectMapper mapper = new ObjectMapper();
        String json = "";

        try {
            json = mapper.writeValueAsString(req);
            log.info("FINAL REQUEST JSON = {}", new ObjectMapper().writeValueAsString(req));
            log.info("REQUEST JSON STRING = {}", json);
        } catch (Exception e) {
            log.error("JSON 변환 실패", e);
        }

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        try {
            log.info("REQUEST JSON STRING = {}", mapper.writeValueAsString(req));
        } catch (Exception e) {
            log.error("JSON 변환 실패", e);
        }

        ResponseEntity<LsPlaceResDto> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        LsPlaceResDto.class
                );

        log.info("LS RESPONSE = {}", response.getBody());

        return response.getBody();
    }



    /**
     * 상품 조회
     */
    public LsProductResDto getProducts() {

        String url = properties.getBaseUrl() + "/product";

        // 요청 DTO
        LsProductReqDto req = new LsProductReqDto();
        LsProductReqDto.Data data = new LsProductReqDto.Data();
        data.setAgentNo(properties.getAgentNo());
        data.setType("all");   // 전체조회
        req.setData(data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", properties.getToken());

        ObjectMapper mapper = new ObjectMapper();
        String json = "";

        try {
            json = mapper.writeValueAsString(req);
            log.info("LS REQUEST JSON = {}", json);
        } catch (Exception e) {
            log.error("JSON 변환 실패", e);
        }

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        ResponseEntity<LsProductResDto> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        LsProductResDto.class
                );

        log.info("LS RESPONSE = {}", response.getBody());

        return response.getBody();
    }


    /**
     * 티켓 발권
     */
    public LsIssueResDto issue(LsIssueReqDto req) {

        String url = properties.getBaseUrl() + "/issue";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", properties.getToken());

        ObjectMapper mapper = new ObjectMapper();
        String json = "";

        try {
            json = mapper.writeValueAsString(req);
            log.info("LS ISSUE REQUEST JSON = {}", json);
        } catch (Exception e) {
            log.error("JSON 변환 실패", e);
        }

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        ResponseEntity<LsIssueResDto> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        LsIssueResDto.class
                );

        log.info("LS ISSUE RESPONSE = {}", response.getBody());

        return response.getBody();
    }
}