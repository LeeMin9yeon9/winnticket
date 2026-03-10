package kr.co.winnticket.integration.aquaplanet.client;

import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;

@Component
public class AquaPlanetClient {
    private final RestTemplate restTemplate;
    @Value("${aquaplanet.url}") private String apiUrl;

    // 생성자에서 RestTemplate의 설정을 변경합니다.
    public AquaPlanetClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // 1. JSON 변환기 생성
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        // 2. 한화 서버가 뱉는 text/plain 형식을 JSON으로 읽을 수 있게 매체 타입 추가
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.TEXT_PLAIN, // 이 부분이 핵심입니다.
                new MediaType("text", "plain", StandardCharsets.UTF_8)
        ));

        // 3. 기존 컨버터 리스트의 맨 앞에 추가하여 우선순위를 높임
        this.restTemplate.getMessageConverters().add(0, converter);
    }

    public <T> Map execute(String svcCd, String intfId, T data) {
        AquaPlanetRequest<T> req = new AquaPlanetRequest<>();
        req.setSystemHeader(buildHeader(svcCd, intfId));
        req.setTransactionHeader(new AquaPlanetRequest.TransactionHeader());
        req.setData(data);

        // 이제 text/plain 응답도 Map.class로 자동 변환됩니다.
        return restTemplate.postForObject(apiUrl, req, Map.class);
    }

    private AquaPlanetRequest.SystemHeader buildHeader(String svcCd, String intfId) {
        AquaPlanetRequest.SystemHeader sh = new AquaPlanetRequest.SystemHeader();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        sh.setTMSG_WRTG_DT(now.substring(0, 8));
        sh.setFRS_RQST_DTM(now);
        sh.setTMSG_RQST_DTM(now);
        sh.setTMSG_CRE_SYS_NM("SIF12345"); // 성공했던 시스템명
        sh.setSTN_TMSG_IP("13.109.91.167"); // 성공했던 IP
        sh.setRECV_SVC_CD(svcCd);
        sh.setINTF_ID(intfId);
        sh.setSTD_TMSG_SEQ_NO("1" + System.currentTimeMillis() / 1000);
        return sh;
    }
}