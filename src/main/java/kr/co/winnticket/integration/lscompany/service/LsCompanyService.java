package kr.co.winnticket.integration.lscompany.service;

import kr.co.winnticket.integration.lscompany.client.LsCompanyClient;
import kr.co.winnticket.integration.lscompany.dto.*;
import kr.co.winnticket.integration.lscompany.mapper.LsCompanyMapper;
import kr.co.winnticket.integration.lscompany.props.LsCompanyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class LsCompanyService {

    private final LsCompanyClient client;
    private final LsCompanyProperties properties;
    private final LsCompanyMapper mapper;

    // 시설 조회
    public LsPlaceResDto getPlaces() {

        return client.getPlaces();
    }

    // 상품 조회
    public LsProductResDto getProducts(){

        return client.getProducts();
    }

    // 티켓 발권
    public LsIssueResDto issueTicket(UUID orderId) {

        LsIssueReqDto req = mapper.selectLsIssueRequest(orderId);

        if (req == null || req.getData() == null) {
            throw new RuntimeException("LS 발권 주문 데이터가 없습니다. orderId=" + orderId);
        }

        if (req.getData().getOrder() == null || req.getData().getOrder().isEmpty()) {
            throw new RuntimeException("LS 발권 대상 티켓이 없습니다. orderId=" + orderId);
        }

        // 업체코드
        req.getData().setAgentNo(properties.getAgentNo());

        // 발권일자
        req.getData().setDate(
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );

        // 티켓 수신자 정보 기본값
        if (req.getData().getName() == null || req.getData().getName().isBlank()) {
            req.getData().setName(req.getData().getOrderName());
        }

        if (req.getData().getHp() == null || req.getData().getHp().isBlank()) {
            req.getData().setHp(req.getData().getOrderHp());
        }

        return client.issue(req);
    }
}





