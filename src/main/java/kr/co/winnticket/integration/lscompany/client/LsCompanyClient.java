package kr.co.winnticket.integration.lscompany.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.lscompany.dto.*;
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

    private final RestTemplate restTemplate;
    private final LsCompanyProperties properties;
    private final ObjectMapper objectMapper;

        public LsPlaceResDto getPlaces() {

            String url = properties.getBaseUrl() + "/place";

            LsPlaceReqDto req = new LsPlaceReqDto();
            LsPlaceReqDto.Data data = new LsPlaceReqDto.Data();
            data.setAgentNo(properties.getAgentNo());
            req.setData(data);

            HttpHeaders headers = new HttpHeaders();
            //headers.setContentType(new MediaType("application", "json", java.nio.charset.StandardCharsets.UTF_8));
            headers.setContentType(MediaType.APPLICATION_JSON);
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

            ResponseEntity<LsPlaceResDto> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            LsPlaceResDto.class
                    );

            return response.getBody();
        }


    // 상품 조회
        public LsProductResDto getProducts(String productCode) {

            String url = properties.getBaseUrl() + "/product";

            // 요청 DTO
            LsProductReqDto req = new LsProductReqDto();
            LsProductReqDto.Data data = new LsProductReqDto.Data();
            data.setAgentNo(properties.getAgentNo());
            if (productCode != null) {
                data.setType("single");
                data.setProductCode(productCode);
            } else {
                data.setType("all");
            }
            req.setData(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setContentType(new MediaType("application", "json", java.nio.charset.StandardCharsets.UTF_8));
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

        // 티켓 발권
        public LsIssueResDto issue(LsIssueReqDto req) {

            String url = properties.getBaseUrl() + "/issue";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
           // headers.setContentType(new MediaType("application", "json", java.nio.charset.StandardCharsets.UTF_8));
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

        // 티켓 상태조회
        public LsStatusResDto inquiryTicket(String transactionId){

            String url = properties.getBaseUrl() + "/inquiry";

            LsStatusReqDto req = new LsStatusReqDto();
            LsStatusReqDto.Data data = new LsStatusReqDto.Data();

            data.setAgentNo(properties.getAgentNo());
            data.setTransactionId(transactionId);

            req.setData(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
           // headers.setContentType(new MediaType("application", "json", java.nio.charset.StandardCharsets.UTF_8));
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.add("Authorization", properties.getToken());

            String json = "";

            try {
                json = objectMapper.writeValueAsString(req);
                log.info("LS inquiry request = {}", json);
            } catch (Exception e) {
                log.error("JSON 변환 실패", e);
            }
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
//            HttpEntity<LsStatusReqDto> entity = new HttpEntity<>(req, headers);

            ResponseEntity<LsStatusResDto> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            LsStatusResDto.class
                    );

            log.info("LS inquiry response = {}", response.getBody());

            return response.getBody();
        }

        // 티켓 발권 취소
        public LsCancelResDto cancelTicket(String transactionId){

            String url = properties.getBaseUrl() + "/cancel";

            LsCancelReqDto req = new LsCancelReqDto();
            LsCancelReqDto.Data data = new LsCancelReqDto.Data();

            data.setAgentNo(properties.getAgentNo());
            data.setTransactionId(transactionId);

            req.setData(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            //headers.setContentType(new MediaType("application", "json", java.nio.charset.StandardCharsets.UTF_8));
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.add("Authorization", properties.getToken());

            String json = "";

            try {
                json = objectMapper.writeValueAsString(req);
                log.info("LS cancel request = {}", json);
            } catch (Exception e) {
                log.error("JSON 변환 실패", e);
            }

            //HttpEntity<LsCancelReqDto> entity = new HttpEntity<>(req, headers);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            ResponseEntity<LsCancelResDto> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            LsCancelResDto.class
                    );

            log.info("LS cancel response = {}", response.getBody());

            return response.getBody();
        }

        // 티켓 재전송
        public LsResendResDto resendTicket(String orderNumber){

            String url = properties.getBaseUrl() + "/resend";

            LsResendReqDto req = new LsResendReqDto();
            LsResendReqDto.Data data = new LsResendReqDto.Data();

            data.setAgentNo(properties.getAgentNo());
            data.setOrderNo(orderNumber);

            req.setData(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.add("Authorization", properties.getToken());

            String json = "";

            try {
                json = objectMapper.writeValueAsString(req);
                log.info("LS resend request = {}", json);
            } catch (Exception e) {
                log.error("JSON 변환 실패", e);
            }

            //HttpEntity<LsResendReqDto> entity = new HttpEntity<>(req, headers);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);

            ResponseEntity<LsResendResDto> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            LsResendResDto.class
                    );

            log.info("LS resend response = {}", response.getBody());

            return response.getBody();
        }
    }
