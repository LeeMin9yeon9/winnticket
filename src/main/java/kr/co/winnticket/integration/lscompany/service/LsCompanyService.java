package kr.co.winnticket.integration.lscompany.service;

import kr.co.winnticket.integration.lscompany.client.LsCompanyClient;
import kr.co.winnticket.integration.lscompany.dto.LsPlaceReqDto;
import kr.co.winnticket.integration.lscompany.dto.LsPlaceResDto;
import kr.co.winnticket.integration.lscompany.props.LsCompanyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class LsCompanyService {
    private final LsCompanyClient client;
    private final LsCompanyProperties properties;

    public LsPlaceResDto getPlaces() {

        LsPlaceReqDto req = new LsPlaceReqDto();

        LsPlaceReqDto.DataObj data = new LsPlaceReqDto.DataObj();
        data.setAgentNo(properties.getAgentNo());

        req.setData(data);

        return client.post("place", req, LsPlaceResDto.class);
    }

}
