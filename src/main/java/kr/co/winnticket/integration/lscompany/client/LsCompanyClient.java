package kr.co.winnticket.integration.lscompany.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.lscompany.dto.LsPlaceReqDto;
import kr.co.winnticket.integration.lscompany.dto.LsPlaceResDto;
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

    public LsPlaceResDto getPlaces() {

        String url = properties.getBaseUrl() + "/place";

        LsPlaceReqDto req = new LsPlaceReqDto();
        LsPlaceReqDto.Data data = new LsPlaceReqDto.Data();
        data.setAgentNo(properties.getAgentNo());
        req.setData(data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", properties.getToken());

        ObjectMapper mapper = new ObjectMapper();
        String json = "";

        try {
            json = mapper.writeValueAsString(req);
            log.info("REQUEST JSON STRING = {}", json);
        } catch (Exception e) {
            log.error("JSON 변환 실패", e);
        }

        HttpEntity<LsPlaceReqDto> entity = new HttpEntity<>(req, headers);


        try {
            log.info("REQUEST JSON STRING = {}", mapper.writeValueAsString(req));
        } catch (Exception e) {
            log.error("JSON 변환 실패", e);
        }

        log.info("LS URL = {}", url);
        log.info("REQUEST JSON = {}", req);

        ResponseEntity<LsPlaceResDto> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        LsPlaceResDto.class
                );

        log.info("LS RESPONSE = {}", response.getBody());

        return response.getBody();
}

}



