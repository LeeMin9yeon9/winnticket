package kr.co.winnticket.integration.plusn.service;

import jakarta.transaction.Transactional;
import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.plusn.client.PlusNClient;
import kr.co.winnticket.integration.plusn.dto.*;
import kr.co.winnticket.integration.plusn.mapper.PlusNMapper;
import kr.co.winnticket.integration.plusn.mapper.PlusNResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlusNService {

    private final PlusNClient client;
    private final PlusNMapper mapper;
    private final PlusNResponseMapper responseMapper;

    // =========================
    // 주문
    // =========================
    public PlusNOrderResponse order(UUID orderId) {
        PlusNOrderRequest req = mapper.selectPlusNOrderBase(orderId);

        PlusNOrderResponse res = client.order(req);

        validate(
                responseMapper.mapOrder(res),
                "PlusN 주문 실패"
        );

        List<PlusNOrderResponse.Coupon> coupons = res.getCoupon();
        List<PlusNTicket> tickets = mapper.selectTicketsForPlusN(orderId);

        Map<String, Queue<PlusNOrderResponse.Coupon>> couponMap =
                coupons.stream()
                        .collect(Collectors.groupingBy(
                                PlusNOrderResponse.Coupon::getGoods_code,
                                Collectors.toCollection(LinkedList::new)
                        ));

        for (PlusNTicket ticket : tickets) {

            Queue<PlusNOrderResponse.Coupon> queue =
                    couponMap.get(ticket.getGoodsCode());

            if (queue == null || queue.isEmpty()) {
                throw new IllegalStateException(
                        "PlusN coupon 매칭 실패 goods_code=" + ticket.getGoodsCode()
                );
            }

            PlusNOrderResponse.Coupon coupon = queue.poll();

            mapper.updateTicketOrderSales(
                    ticket.getTicketId(),
                    coupon.getOrder_sales(),
                    coupon.getCoupon_no()
            );
        }

        return res;
    }

    @Transactional
    public PlusNBatchCancelResponse cancel(UUID orderId) {

        log.info("[PlusN] cancel start orderId={}", orderId);

        List<PlusNCancelRequest> tickets =
                mapper.selectPlusNCancel(orderId);

        if (tickets == null || tickets.isEmpty()) {
            return PlusNBatchCancelResponse.fail("취소 대상이 없습니다.");
        }

        // =========================
        // 1. 전체 취소 가능 여부 확인
        // =========================
        for (PlusNCancelRequest req : tickets) {

            PlusNInquiryRequest inquiry = new PlusNInquiryRequest();
            inquiry.setOrder_id(req.getOrder_id());
            inquiry.setOrder_sales(req.getOrder_sales());

            PlusNInquiryResponse inquiryRes = client.inquiry(inquiry);

            String code = inquiryRes.getReturn_div();

            if (!"0000".equals(code) && !"0005".equals(code)) {

                log.error("[PlusN] 취소 불가 order_sales={}, message={}",
                        req.getOrder_sales(),
                        inquiryRes.getReturn_msg());

                return PlusNBatchCancelResponse.fail(
                        "취소 불가: " + inquiryRes.getReturn_msg()
                );
            }

            // 사용된 쿠폰
            if (inquiryRes.getResult_date() != null) {
                log.error("[PlusN] 사용된 쿠폰 order_sales={}",
                        req.getOrder_sales());

                return PlusNBatchCancelResponse.fail(
                        "사용된 쿠폰이 포함되어 취소할 수 없습니다."
                );
            }
        }

        // =========================
        // 2. 전체 취소 실행
        // =========================
        List<String> canceledTickets = new ArrayList<>();

        for (PlusNCancelRequest req : tickets) {

            log.info("[PlusN] cancel try order_sales={}", req.getOrder_sales());

            try {

                PlusNCancelResponse cancelRes = client.cancel(req);

                log.info("[PlusN] cancel response code={}", cancelRes.getReturn_div());

                if (!"0000".equals(cancelRes.getReturn_div())) {

                    return PlusNBatchCancelResponse.fail(
                            "취소 실패: " + cancelRes.getReturn_msg()
                    );
                }

                canceledTickets.add(req.getOrder_sales());
            } catch (Exception e) {

                log.error("[PlusN] cancel exception order_sales={}", req.getOrder_sales(), e);
                throw e;
            }

        }

        log.info("[PlusN] cancel success orderId={}, count={}",
                orderId, canceledTickets.size());

        return PlusNBatchCancelResponse.success(canceledTickets);
    }

    // =========================
    // 날짜별 사용조회
    // =========================
    public PlusNUsedDateResponse usedDate(String yyyymmdd) {

        PlusNUsedDateRequest req =
                new PlusNUsedDateRequest();

        req.setOrder_date(yyyymmdd);

        PlusNUsedDateResponse res =
                client.usedDate(req);

        return res;
    }

    // =========================
    // 공통 검증
    // =========================
    private void validate(IntegrationResult result,
                          String defaultMessage) {

        if (!result.isSuccess()) {
            throw new RuntimeException(
                    defaultMessage
                            + " - code: "
                            + result.getCode()
                            + ", message: "
                            + result.getMessage()
            );
        }
    }
}