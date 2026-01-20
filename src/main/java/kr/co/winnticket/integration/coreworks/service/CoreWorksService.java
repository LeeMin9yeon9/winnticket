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
        req.setName("홍길동");
        req.setHp("01012341234");

        CWOrderRequest.Item item = new CWOrderRequest.Item();
        item.setPin("");
        item.setItemCode("hdt_25");
        item.setItemName("민속촌 자유이용권");

        req.setItemList(List.of(item));

        return client.order(req);
    }

    // 조회
    public CWSearchResponse testSearch(CWSearchRequest req) {
        req.setChannelCd(props.getChannelCd());
        req.setOrderSeq(req.getOrderSeq());

        CWSearchRequest.Pin pin = new CWSearchRequest.Pin();
        pin.setPin(pin.getPin());
        return client.search(req);
    }

    // 취소
    public CWCancelResponse testCancel(CWCancelRequest req) {
        req.setChannelCd(props.getChannelCd());
        req.setOrderSeq(req.getOrderSeq());

        CWSearchRequest.Pin pin = new CWSearchRequest.Pin();
        pin.setPin(pin.getPin());
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

}
