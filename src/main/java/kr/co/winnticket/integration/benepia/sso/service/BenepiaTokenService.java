package kr.co.winnticket.integration.benepia.sso.service;

import kr.co.winnticket.integration.benepia.common.BenepiaProperties;
import kr.co.winnticket.integration.benepia.crypto.BenepiaSeedEcbCrypto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class BenepiaTokenService {

    private final BenepiaProperties properties;
    private BenepiaSeedEcbCrypto seedEcbCrypto;
    private final RestTemplate restTemplate = new RestTemplate();

    // 베네피아 SSO 토큰 생성 요청
    public String createToken(String loginid){

        String plain = "loginid=" +loginid;

       // String encParam = seedEcbCrypto.encrypt(plain);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("custCoCd", properties.getCustCoCd()); // z381
     //   body.add("encParam", encParam);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        properties.getTokenCreateUrl(),
                        request,
                        String.class
                );

        return response.getBody();
    }
}
