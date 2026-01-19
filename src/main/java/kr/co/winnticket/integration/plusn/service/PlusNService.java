package kr.co.winnticket.integration.plusn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.plusn.client.PlusNClient;
import kr.co.winnticket.integration.plusn.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlusNService {

    private final PlusNClient client;

    @Value("${plusn.order-company}")
    private String orderCompany;

    // 주문 테스트
    public PlusNOrderResponse testOrder() {

        PlusNOrderRequest req = new PlusNOrderRequest();
        req.setOrder_id("TEST_" + System.currentTimeMillis());
        req.setUser_name("홍길동");
        req.setUser_hp("01012341234");
        req.setUser_email("test@test.com");
        req.setOrder_date(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        PlusNOrderRequest.ClassDiv div = new PlusNOrderRequest.ClassDiv();
        div.setGubun("A");
        div.setGoods_code("96334");
        div.setCnt("1");
        div.setSelected_date("");

        req.setClass_div(List.of(div));

        return client.order(req);
    }

    // 취소 테스트
    public PlusNCancelResponse testCancel(String orderId, String orderSales) {
        PlusNCancelRequest req = new PlusNCancelRequest();
        req.setOrder_id(orderId);
        req.setOrder_sales(orderSales);
        req.setResult_date(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        return client.cancel(req);
    }

    // 티켓 조회 테스트
    public PlusNInquiryResponse testInquiry(String orderId, String orderSales) {
        PlusNInquiryRequest req = new PlusNInquiryRequest();
        req.setOrder_id(orderId);
        req.setOrder_sales(orderSales);

        return client.inquiry(req);
    }

    // 날짜별 사용조회 테스트
    public PlusNUsedDateResponse testUsedDate(String yyyymmdd) {
        PlusNUsedDateRequest req = new PlusNUsedDateRequest();
        req.setOrder_date(yyyymmdd);

        return client.usedDate(req);
    }
}