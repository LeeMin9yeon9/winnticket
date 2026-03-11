package kr.co.winnticket.integration.aquaplanet.service;

import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AquaPlanetService {
    private final RestTemplate restTemplate;
    private final String API_URL = "https://exgatedev.hanwharesort.co.kr:443/iGate/SIF/json.jdo";

    public String aquaPlanetRequest(String svcCd, String corpCd, Map<String, Object> dataBody, String dataKey) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nowTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String seqNo = String.valueOf(System.currentTimeMillis());

        // 1. 헤더 세팅 (성공했던 curl 설정 복사)
        AquaPlanetRequest.SystemHeader sh = AquaPlanetRequest.SystemHeader.builder()
                .TMSG_WRTG_DT(today)
                .STD_TMSG_SEQ_NO(seqNo)
                .FRS_RQST_DTM(nowTime)
                .TMSG_RQST_DTM(nowTime)
                .RECV_SVC_CD(svcCd)
                .INTF_ID("SIF00" + svcCd)
                .build();

        AquaPlanetRequest.TransactionHeader th = AquaPlanetRequest.TransactionHeader.builder()
                .CORP_CD(corpCd)
                .build();

        // 2. 바디 구성
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(dataKey, Collections.singletonList(dataBody));

        AquaPlanetRequest.Request<Map<String, Object>> request = new AquaPlanetRequest.Request<>();
        request.setSystemHeader(sh);
        request.setTransactionHeader(th);
        request.setData(dataMap);

        // 3. 실행
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AquaPlanetRequest.Request<?>> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(API_URL, entity, String.class);
    }
}