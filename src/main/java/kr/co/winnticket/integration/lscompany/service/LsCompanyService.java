package kr.co.winnticket.integration.lscompany.service;

import kr.co.winnticket.integration.lscompany.client.LsCompanyClient;
import kr.co.winnticket.integration.lscompany.dto.LsPlaceResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class LsCompanyService {

    private final LsCompanyClient client;

    public LsPlaceResDto getPlaces() {

        log.info("LS 시설 조회 요청");

        return client.getPlaces();
    }
}





