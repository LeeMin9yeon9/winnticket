package kr.co.winnticket.integration.coreworks.service;

import kr.co.winnticket.integration.coreworks.client.CoreWorksClient;
import kr.co.winnticket.integration.coreworks.dto.*;
import kr.co.winnticket.integration.coreworks.props.CoreWorksProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoreWorksService {

    private final CoreWorksClient client;
    private final CoreWorksProperties props;

    // 테스트용 주문 생성
    public CWOrderResponse testOrder() {

        CWOrderRequest req = new CWOrderRequest();
        req.setChannelCd(props.getChannelCd());
        req.setOrderSeq("CW_TEST_" + System.currentTimeMillis());
        req.setBuyDate(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        req.setName("이민걍");
        req.setHp("01094618018");

        CWOrderRequest.Item item = new CWOrderRequest.Item();
        item.setPin("");
        item.setItemCode("hdt_25");
        item.setItemName("민속촌 자유이용권");

        req.setItemList(List.of(item));

        return client.order(req);
    }

    // 조회
    public CWSearchResponse testSearch(CWSearchRequest req) {
        CWSearchRequest newReq = new CWSearchRequest();
        newReq.setChannelCd(props.getChannelCd());
        newReq.setOrderSeq(req.getOrderSeq());

        CWSearchRequest.Pin pinObj = new CWSearchRequest.Pin();
        pinObj.setPin(req.getPinList().get(0).getPin());
        newReq.setPinList(List.of(pinObj));
        return client.search(req);
    }

    // 취소
    public CWCancelResponse testCancel(CWCancelRequest req) {
        CWCancelRequest newReq = new CWCancelRequest();
        newReq.setChannelCd(props.getChannelCd());
        newReq.setOrderSeq(req.getOrderSeq());

        CWCancelRequest.Pin pinObj = new CWCancelRequest.Pin();
        pinObj.setPin(req.getPinList().get(0).getPin());
        newReq.setPinList(List.of(pinObj));
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
    public CWMmsResendResponse testMmsResend(String orderSeq, String hp) {
        CWMmsResendRequest req = new CWMmsResendRequest();
        req.setChannelCd(props.getChannelCd());
        req.setOrderSeq(orderSeq);
        req.setHp(hp);
        return client.mmsResend(req);
    }
}
