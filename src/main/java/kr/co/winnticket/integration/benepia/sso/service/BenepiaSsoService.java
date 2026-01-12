package kr.co.winnticket.integration.benepia.sso.service;

import kr.co.winnticket.integration.benepia.common.BenepiaProperties;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaSsoResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class BenepiaSsoService {

    private final BenepiaProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    public boolean confirm(String tknKey) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("custCoCd", properties.getCustCoCd());
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

        return res != null && "S000".equals(res.getResponseCode());
    }
}
