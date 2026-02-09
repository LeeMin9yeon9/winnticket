package kr.co.winnticket.integration.bankda.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.bankda.dto.BankdaRequest;
import kr.co.winnticket.integration.bankda.dto.BankdaResponse;
import kr.co.winnticket.integration.bankda.dto.BankdaTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankdaClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${bankda.url}")
    private String url;

    @Value("${bankda.token}")
    private String token;

    public List<BankdaTransaction> getTransactions(BankdaRequest req) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        headers.set("Authorization", "Bearer " + token);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        if (req.getAccountnum() != null)
            body.add("accountnum", req.getAccountnum());

        if (req.getDatefrom() != null)
            body.add("datefrom", req.getDatefrom());

        if (req.getDateto() != null)
            body.add("dateto", req.getDateto());

        if (req.getBkcode() != null)
            body.add("bkcode", req.getBkcode());

        body.add("datatype", "json");
        body.add("charset", "utf8");
        body.add("istest", req.getIstest() == null ? "n" : req.getIstest());

        HttpEntity<MultiValueMap<String, Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

        String raw = response.getBody();

        log.info("BANKDA RAW = {}", raw);

        try {

            BankdaResponse res =
                    objectMapper.readValue(raw, BankdaResponse.class);

            return res.getResponse().getBank();

        } catch (Exception e) {

            log.error("Bankda 파싱 실패 raw={}", raw, e);
            throw new RuntimeException("Bankda 응답 파싱 실패", e);
        }
    }
}
