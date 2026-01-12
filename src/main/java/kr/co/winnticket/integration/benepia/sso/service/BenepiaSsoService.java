package kr.co.winnticket.integration.benepia.sso.service;

import kr.co.winnticket.integration.benepia.common.BenepiaProperties;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaSsoResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
@RequiredArgsConstructor
public class BenepiaSsoService {

    private final BenepiaProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    public BenepiaSsoResDto confirm(String tknKey) {


        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("custCoCd", properties.getCustCoCd()); // z381
        body.add("tknKey", tknKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<BenepiaSsoResDto> response =
                restTemplate.exchange(
                        properties.getConfirmUrl(),
                        HttpMethod.POST,
                        request,
                        BenepiaSsoResDto.class
                );

        BenepiaSsoResDto res = response.getBody();

        // 🔥 로그 (서버에서도 확인 가능)
        if (res != null) {
            log.info("[BENEP SSO CONFIRM] code={}, message={}",
                    res.getResponseCode(), res.getResponseMessage());
        } else {
            log.error("[BENEP SSO CONFIRM] response is null");
        }

        return res;
    }
}

