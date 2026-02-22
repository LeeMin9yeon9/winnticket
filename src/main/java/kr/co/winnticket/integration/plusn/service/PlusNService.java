package kr.co.winnticket.integration.plusn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.plusn.client.PlusNClient;
import kr.co.winnticket.integration.plusn.dto.*;
import kr.co.winnticket.integration.plusn.mapper.PlusNMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlusNService {

    private final PlusNClient client;
    private final PlusNMapper mapper;

    @Value("${plusn.order-company}")
    private String orderCompany;

    // 주문
    public PlusNOrderResponse order(UUID orderId) {
        PlusNOrderRequest req = mapper.selectPlusNOrderBase(orderId);
        return client.order(req);
    }

    // 취소
    public PlusNBatchCancelResponse cancel(UUID orderId) {
        List<PlusNCancelRequest> tickets = mapper.selectPlusNCancel(orderId);
        List<PlusNCancelResponse> results = new ArrayList<>();

        PlusNBatchCancelResponse response = new PlusNBatchCancelResponse();
        response.setOrderId(orderId.toString());

        if (tickets == null || tickets.isEmpty()) {
            response.setResults(results);
            return response;
        }

        // 전부 0005인지 조회로 확인
        for (PlusNCancelRequest req : tickets) {
            PlusNInquiryRequest reqInquiry = new PlusNInquiryRequest();
            reqInquiry.setOrder_id(req.getOrder_id());
            reqInquiry.setOrder_sales(req.getOrder_sales());
            PlusNInquiryResponse inquiryRes = client.inquiry(reqInquiry);

            if (!"0005".equals(inquiryRes.getReturn_div())) {
                // 하나라도 0005 아니면 취소 진행 안 함
                response.setResults(results); // 빈 리스트 반환
                return response;
            }
        }

        // 전부 0005면 취소 진행
        for (PlusNCancelRequest req : tickets) {
            PlusNCancelResponse cancelRes = client.cancel(req);
            results.add(cancelRes);
        }

        response.setResults(results);
        return response;
    }

    // 날짜별 사용조회 테스트
    public PlusNUsedDateResponse testUsedDate(String yyyymmdd) {
        PlusNUsedDateRequest req = new PlusNUsedDateRequest();
        req.setOrder_date(yyyymmdd);

        return client.usedDate(req);
    }
}