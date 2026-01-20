package kr.co.winnticket.integration.coreworks.service;

import kr.co.winnticket.integration.coreworks.client.CoreWorksClient;
import kr.co.winnticket.integration.coreworks.dto.CWOrderRequest;
import kr.co.winnticket.integration.coreworks.dto.CWOrderResponse;
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
}
