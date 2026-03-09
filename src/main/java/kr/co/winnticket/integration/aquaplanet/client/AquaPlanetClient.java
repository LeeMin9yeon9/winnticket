package kr.co.winnticket.integration.aquaplanet.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
@RequiredArgsConstructor
public class AquaPlanetClient {
    private final RestTemplate restTemplate;
    @Value("${aquaplanet.url}") private String apiUrl;

    public <T> Map execute(String svcCd, String intfId, T data) {
        AquaPlanetRequest<T> req = new AquaPlanetRequest<>();
        req.setSystemHeader(buildHeader(svcCd, intfId));
        req.setTransactionHeader(new AquaPlanetRequest.TransactionHeader());
        req.setData(data);
        return restTemplate.postForObject(apiUrl, req, Map.class);
    }

    private AquaPlanetRequest.SystemHeader buildHeader(String svcCd, String intfId) {
        AquaPlanetRequest.SystemHeader sh = new AquaPlanetRequest.SystemHeader();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        sh.setTMSG_WRTG_DT(now.substring(0, 8));
        sh.setFRS_RQST_DTM(now);
        sh.setTMSG_RQST_DTM(now);
        sh.setTMSG_CRE_SYS_NM("SIF" + (int)(Math.random() * 90000));
        sh.setSTD_TMSG_SEQ_NO("1" + System.currentTimeMillis());
        sh.setRECV_SVC_CD(svcCd);
        sh.setINTF_ID(intfId);
        return sh;
    }
}