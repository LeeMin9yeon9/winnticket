package kr.co.winnticket.integration.coreworks.service;

import kr.co.winnticket.integration.coreworks.client.CoreWorksClient;
import kr.co.winnticket.integration.coreworks.dto.*;
import kr.co.winnticket.integration.coreworks.mapper.CoreWorksMapper;
import kr.co.winnticket.integration.coreworks.props.CoreWorksProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoreWorksService {

    private final CoreWorksClient client;
    private final CoreWorksProperties props;
    private final CoreWorksMapper mapper;

    // 주문
    public CWOrderResponse testOrder(UUID orderId) {
        CWOrderRequest req =  mapper.selectCoreworksOrder(orderId);
        req.setChannelCd(props.getChannelCd());
        return client.order(req);
    }

    // 조회
    public CWSearchResponse testSearch(UUID orderId) {
        CWSearchRequest req = mapper.selectCoreworksSearch(orderId);
        req.setChannelCd(props.getChannelCd());
        return client.search(req);
    }

    // 취소
    public CWCancelResponse testCancel(UUID orderId) {
        CWCancelRequest req = mapper.selectCoreworksCancel(orderId);
        req.setChannelCd(props.getChannelCd());
        return client.cancel(req);
    }

    // 사용조회
    public CWUseSearchResponse testUseSearch(String start, String end) {
        CWUseSearchRequest req = new CWUseSearchRequest();
        req.setChannelCd(props.getChannelCd());
        req.setUseStartDate(start);
        req.setUseEndDate(end);
        return client.useSearch(req);
    }

    // 티켓문자 재발송
    public CWMmsResendResponse testMmsResend(UUID orderId) {
        CWMmsResendRequest req = mapper.selectCoreworksMmsResend(orderId);
        req.setChannelCd(props.getChannelCd());
        return client.mmsResend(req);
    }
}
