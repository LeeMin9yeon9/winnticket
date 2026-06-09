package kr.co.winnticket.integration.smartinfini.service;

import kr.co.winnticket.integration.smartinfini.client.SmartInfiniClient;
import kr.co.winnticket.integration.smartinfini.dto.*;
import kr.co.winnticket.integration.smartinfini.mapper.SmartInfiniMapper;
import kr.co.winnticket.integration.smartinfini.mapper.SmartInfiniResponseMapper;
import kr.co.winnticket.integration.smartinfini.props.SmartInfiniProperties;
import kr.co.winnticket.order.admin.dto.OrderTicketDetailGetResDto;
import kr.co.winnticket.ticket.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static kr.co.winnticket.common.dto.ApiResponse.success;
import static kr.co.winnticket.integration.common.IntegrationResult.fail;
import static org.apache.poi.ss.util.DateParser.parseDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartInfiniService {

    private final SmartInfiniClient client;
    private final SmartInfiniMapper mapper;
    private final TicketMapper ticketMapper;
    private final SmartInfiniProperties props;
    private final SmartInfiniResponseMapper responseMapper;

    // =========================
    // 주문
    // =========================
    public SIOrderResponse order(UUID orderId) {

        SIOrderRequest req = mapper.selectSmartinfiniOrder(orderId);
        req.setChannelCode(props.getChannelId());
        log.info("SmartInfini request = {}", req);
        SIOrderResponse res = client.order(req);
        log.info("SmartInfini response = {}", res);
        var result = responseMapper.map(res.getReturnDiv(), res.getReturnMsg());

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "SmartInfini 주문 실패: " + result.getMessage()
            );
        }

        if (res.getCoupon() == null || res.getCoupon().isEmpty()) {
            throw new RuntimeException("SmartInfini 발권 응답에 쿠폰이 없습니다.");
        }

        for (SIOrderResponse.Coupon c : res.getCoupon()) {
            mapper.updatePartnerOrderInfo(
                    c.getTicketCode(),
                    c.getCouponNo(),   // partnerOrderCode
                    c.getOrderSales()  // partnerOrderNumber
            );
        }

        return res;
    }

    // =========================
    // 조회(단건)
    // =========================
    public SISearchResponse search(SISearchRequest req) {

        SISearchResponse res = client.search(req);

        var result =
                responseMapper.map(res.getReturnDiv(), res.getReturnMsg());

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "SmartInfini 조회 실패: " + result.getMessage()
            );
        }

        return res;
    }

    // =========================
    // 조회(다건)
    // =========================
    public SIOrderSearchResponse searchByOrderNo(UUID orderId) {

        SIOrderSearchRequest req =
                mapper.selectSmartinfinisearchByOrderNo(orderId);
        log.info("SmartInfini req = {}", req);
        SIOrderSearchResponse res =
                client.searchByOrderNo(req);
        log.info("SmartInfini res = {}", res);

        if (res == null) {
            throw new RuntimeException(
                    "SmartInfini 주문조회 실패"
            );
        }

        return res;
    }

    // =========================
    // 취소(단건)
    // =========================
    public SICancelResponse cancelSingle(SICancelRequest req) {

        SICancelResponse res = client.cancel(req);

        var result =
                responseMapper.map(res.getReturnDiv(), res.getReturnMsg());

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "SmartInfini 단건취소 실패: " + result.getMessage()
            );
        }

        return res;
    }

    // =========================
    // 취소(다건)
    // =========================
    public SICancelListResponse cancelMulti(UUID orderId) {

        SICancelListRequest cancelReq =
                mapper.selectSmartinfiniCancelList(orderId);

        cancelReq.setResultDate(
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );
        log.info("SmartInfini cancelReq = {}", cancelReq);
        SICancelListResponse res =
                client.cancelList(cancelReq);
        log.info("SmartInfini res = {}", res);

        if (res == null) {
            throw new RuntimeException(
                    "SmartInfini 다건취소 실패"
            );
        }

        return res;
    }

    // =========================
    // 상품조회
    // =========================
    public List<SIProductResponse> product(SIProductRequest req) {
        return client.product(req);
    }

    // =========================
    // 문자 재전송
    // =========================
    public SIMmsResendResponse mmsResend(UUID orderId) {

        SIMmsResendRequest req =
                mapper.selectSmartinfiniMmsResend(orderId);

        SIMmsResendResponse res =
                client.mmsResend(req);

        var result =
                responseMapper.map(res.getReturn_div(), res.getReturn_msg());

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    "SmartInfini 문자 재전송 실패: " + result.getMessage()
            );
        }

        return res;
    }

    // 티켓사용처리
    @Transactional
    public SIUseCallbackResponse use(SIUseCallbackRequest req) {
        // 1. 티켓 조회
        SmartInfiniOrderTicket ticket = ticketMapper.findByTicketCodeSmartInfini(req.getTicketCode());

        if (ticket == null) {
            return fail("티켓 없음");
        }

        // 2. 이미 사용됐는데 사용으로 들어오거나 이미 사용취소인데 사용취소로 들어올경우
        if (ticket.isTicketUsed() && req.getOrderDiv().equals("51")) {
            return fail("이미 사용된 티켓");
        }

        if (!ticket.isTicketUsed() && req.getOrderDiv().equals("50")) {
            return fail("이미 취소된 티켓");
        }

        // 3. 사용 처리
        int updated = ticketMapper.useTicketSmartInfini(
                req.getOrderDiv(),
                req.getTicketCode(),
                req.getResultDate()
        );

        if (updated == 0) {
            return fail("사용 처리 실패");
        }

        return success(ticket.getOrderNumber());
    }

    private SIUseCallbackResponse success(String orderNo) {
        return SIUseCallbackResponse.builder()
                .orderNo(orderNo)
                .returnDiv("S")
                .returnMsg("성공")
                .build();
    }

    private SIUseCallbackResponse fail(String msg) {
        return SIUseCallbackResponse.builder()
                .returnDiv("F")
                .returnMsg(msg)
                .build();
    }
}