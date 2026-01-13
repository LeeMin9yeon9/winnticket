package kr.co.winnticket.integration.benepia.sso.service;

import kr.co.winnticket.integration.benepia.common.BenepiaProperties;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaTokenResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Log4j2
@Service
@RequiredArgsConstructor
// SSO 토큰 요청
public class BenepiaTokenService {

    private final BenepiaProperties properties;
    private final BenepiaTokenEncParamBuilder encParamBuilder;
    private final RestClient restClient = RestClient.create();

    // 토큰 생성
    public String createToken(BenepiaDecryptedParamDto decryptedParamDto){

        String loginId = decryptedParamDto.getBenefit_id(); // 베네피아 ID
        String empNo = decryptedParamDto.getUserid(); // 사번

        String encParam = encParamBuilder.build(loginId,empNo);

        log.info("[BENEPIA]TOKEN REQUEST loginId={}, empNo={}", loginId, empNo);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("encParam", encParam);
        form.add("custCoCd", properties.getCustCoCd());

        BenepiaTokenResDto res = restClient.post()
                .uri(properties.getTokenCreateUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(BenepiaTokenResDto.class);

        if(!"S000".equals(res.getResponseCode())){
            throw new IllegalStateException("TOKEN CREATE FAILED : " + res.getResponseMessage());
        }
        log.info("[BENEPIA] TOKEN CREATE SUCCESS !!!");
        return res.getResponseData().getTknKey();
    }

    // 토큰 확인
    public void confirmToken(String tknKey){
        log.info("[BENEPIA] TOKEN CONFIRM tknKey={}",tknKey);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("tknKey",tknKey);   // 발급받은 TOKEN KEY
        form.add("custCoCd",properties.getCustCoCd());  // 고객사 코드

        BenepiaTokenResDto res = restClient.post()
                .uri(properties.getConfirmUrl())  // 토큰확인 url
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(BenepiaTokenResDto.class);
         if(!"S000".equals(res.getResponseCode())){
            throw new IllegalStateException("TOKEN CONFIRM FAILED : " + res.getResponseMessage());
        }
        log.info("[BENEPIA] TOKEN CONFIRM SUCCESS !!!");
    }
}
