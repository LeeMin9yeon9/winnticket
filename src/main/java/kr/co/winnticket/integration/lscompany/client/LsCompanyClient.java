package kr.co.winnticket.integration.lscompany.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.lscompany.dto.*;
import kr.co.winnticket.integration.lscompany.props.LsCompanyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class LsCompanyClient {

    private final RestTemplate restTemplate;
    private final LsCompanyProperties properties;
    private final ObjectMapper objectMapper;

    public LsPlaceResDto getPlaces() {
        String urlStr = properties.getBaseUrl() + "/place";
        String body = "{\"data\":{\"agentNo\":\"" + properties.getAgentNo() + "\"}}";

        log.info("LS URL = {}", properties.getBaseUrl() + "/place");

        log.info("LS URL = {}", urlStr);
        log.info("LS TOKEN = [{}]", properties.getToken());
        log.info("LS REQUEST BODY = {}", body);

        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);

            // curl 최대한 비슷하게
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", properties.getToken());

            byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

            // 구형 서버 대응: chunked 말고 고정 길이로 보냄
            conn.setFixedLengthStreamingMode(bodyBytes.length);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(bodyBytes);
                os.flush();
            }

            int status = conn.getResponseCode();
            log.info("LS HTTP STATUS = {}", status);

            InputStream is = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            String responseBody;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                responseBody = br.lines().collect(Collectors.joining("\n"));
            }

            log.info("LS RAW RESPONSE = {}", responseBody);

            if (status >= 200 && status < 300) {
                return objectMapper.readValue(responseBody, LsPlaceResDto.class);
            } else {
                throw new RuntimeException("LS API 호출 실패. status=" + status + ", body=" + responseBody);
            }

        } catch (Exception e) {
            log.error("LS API 호출 실패", e);
            throw new RuntimeException("LS API 호출 실패", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


    // 상품 조회
        public LsProductResDto getProducts() {

            String url = properties.getBaseUrl() + "/product";

            // 요청 DTO
            LsProductReqDto req = new LsProductReqDto();
            LsProductReqDto.Data data = new LsProductReqDto.Data();
            data.setAgentNo(properties.getAgentNo());
            data.setType("all");   // 전체조회
            req.setData(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", java.nio.charset.StandardCharsets.UTF_8));
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.add("Authorization", properties.getToken());

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
            headers.setContentType(new MediaType("application", "json", java.nio.charset.StandardCharsets.UTF_8));
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.add("Authorization", properties.getToken());

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
