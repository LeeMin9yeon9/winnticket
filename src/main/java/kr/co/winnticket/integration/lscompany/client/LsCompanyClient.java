package kr.co.winnticket.integration.lscompany.client;

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
        headers.set("Content-Type", "application/json;charset=UTF-8");
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", properties.getToken());

    HttpEntity<LsPlaceReqDto> entity = new HttpEntity<>(req, headers);

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



