package kr.co.winnticket.integration.lscompany.service;

import kr.co.winnticket.integration.lscompany.client.LsCompanyClient;
import kr.co.winnticket.integration.lscompany.dto.*;
import kr.co.winnticket.integration.lscompany.mapper.LsCompanyMapper;
import kr.co.winnticket.integration.lscompany.props.LsCompanyProperties;
import kr.co.winnticket.integration.smartinfini.dto.SIUseCallbackResponse;
import kr.co.winnticket.integration.smartinfini.dto.SmartInfiniOrderTicket;
import kr.co.winnticket.ticket.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class LsCompanyService {

    private final LsCompanyClient client;
    private final LsCompanyProperties properties;
    private final LsCompanyMapper mapper;
    private final TicketMapper ticketMapper;

    // 시설 조회
    public LsPlaceResDto getPlaces() {

        return client.getPlaces();
    }

    // 상품 조회
    public LsProductResDto getProducts(String productCode){
        return client.getProducts(productCode);
    }

    // 티켓 발권
    @Transactional
    public LsIssueResDto issueTicket(UUID orderId) {
        // 주문 정보 조회
        LsOrderInfoDto orderInfo = mapper.selectOrderInfoByOrderId(orderId);

        if (orderInfo == null) {
            throw new RuntimeException("주문정보 없음 orderId=" + orderId);
        }

        log.info("orderInfo = {}", orderInfo);
        String orderNumber = orderInfo.getOrderNumber();

        // 주문 상품 조회
        List<LsOrderItemInfoDto> items = mapper.selectOrderItemInfos(orderId);

        log.info("orderId param = {}", orderId);
        log.info("orderInfo.orderId = {}", orderInfo.getOrderId());

        log.info(" LS 요청용 DB 조회 결과 = {}", items);

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("주문아이템 없음 orderNumber=" + orderNumber);
        }

        // LS 발권 요청 DTO 생성
        LsIssueReqDto req = new LsIssueReqDto();
        LsIssueReqDto.Data data = new LsIssueReqDto.Data();

        // LS 업체코드
        data.setAgentNo(properties.getAgentNo());

        // 주문자 정보
        data.setOrderName(orderInfo.getCustomerName());
        data.setOrderHp(orderInfo.getCustomerPhone());

        // 티켓 수신자 정보 (주문자와 동일)
        data.setName(orderInfo.getCustomerName());
        data.setHp(orderInfo.getCustomerPhone());
        data.setEmail(orderInfo.getCustomerEmail());

        // 발권요청시간 (yyyyMMddHHmmss)
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        data.setDate(now);

        // 윈앤티켓 주문번호
        data.setOrderNo(orderNumber);


        List<LsIssueReqDto.Data.Order> orderList = new ArrayList<>();


        // 쿠폰 수량만큼 발권 요청 생성
            for (LsOrderItemInfoDto item : items) {
                log.info("LS 발권 대상 item - orderItemId={}, productCode={}, optionId=[{}]",
                        item.getOrderItemId(),
                        item.getProductCode(),
                        item.getOptionId()
                );

                LsProductResDto productRes = client.getProducts(item.getProductCode());
                if (productRes != null && productRes.getList() != null && !productRes.getList().isEmpty()) {
                    log.info("LS option 리스트 = {}", productRes.getList().get(0).getOption());
                }

                // 발권 안된 티켓 조회
                List<String> ticketNumbers = mapper.selectOrderTicketNumbers(item.getOrderItemId());

                if (ticketNumbers == null || ticketNumbers.isEmpty()) {
                    log.info("이미 발권 완료된 상품 orderItemId={}", item.getOrderItemId());
                    continue;
                }

                int issueCount = ticketNumbers.size();

                for (int i = 0; i < issueCount; i++) {

                    String ticketNumber = ticketNumbers.get(i);

                    LsIssueReqDto.Data.Order o = new LsIssueReqDto.Data.Order();

                    String optionId = item.getOptionId();


                    if (optionId != null && !optionId.isBlank()) {
                        o.setOptionId(optionId);
                    } else {
                        log.warn("LS optionId 없음 - 해당 파트너 상품이 아닙니다 orderItemId={}", item.getOrderItemId());
                        continue; // 👉 LS 대상 아니면 아예 발권 제외
                    }

                    o.setTransactionId(ticketNumber);         // transactionId = 윈앤티켓 티켓번호
                   // o.setOptionId(item.getOptionId());      // LS 옵션ID
                    o.setPrice(String.valueOf(item.getPrice()));    // 판매가
                    o.setDiscount(item.getDiscount() == null ? "0" : String.valueOf(item.getDiscount()));   // 할인금액

                    orderList.add(o);
                }
            }

            if (orderList.isEmpty()) {
                log.info("LS 발권 대상 없음 orderNumber={}", orderNumber);
                return null;
            }

            data.setOrder(orderList);
            req.setData(data);

            log.info("LS 발권 요청 개수 = {}", orderList.size());
            log.info("LS 발권 요청 = {}", req);

             //LS API 발권 호출
            LsIssueResDto res = client.issue(req);

            log.info("LS 발권 응답 = {}", res);

            if (!"0000".equals(res.getResultCode())) {
                log.error("LS 발권 실패 resultCode={} message={}",
                        res.getResultCode(),
                        res.getResultMessage());
                throw new RuntimeException("LS 발권 실패: " + res.getResultMessage());
            }

            // 바코드 개수 검증
            if (res.getBarcodeArr() == null || res.getBarcodeArr().isEmpty()) {
                throw new RuntimeException("LS 바코드 응답 없음");
            }

            if (res.getBarcodeArr().size() != orderList.size()) {
                log.error("LS 발권 개수 불일치 요청={} 응답={}",
                        orderList.size(),
                        res.getBarcodeArr().size());
            }

             // 발권 성공 시 바코드 저장
        if (res.getBarcodeArr() != null) {
            for (LsIssueResDto.BarcodeArr barcode : res.getBarcodeArr()) {

                mapper.updatePartnerBarcode(
                        barcode.getBarcode(),
                        barcode.getTransactionId()
                );
            }
        }
            log.info("LS 발권 완료 orderNumber={}", orderNumber);
                return res;
            }



        // 티켓 상태조회
        public List<LsStatusResDto> inquiryTicket(UUID orderId) {

            List<String> ticketNumbers = mapper.selectTicketNumbersByOrderId(orderId);

            if (ticketNumbers == null || ticketNumbers.isEmpty()) {
                throw new RuntimeException("조회할 티켓 없음 orderId=" + orderId);
            }

            List<LsStatusResDto> results = new ArrayList<>();

            for (String ticketNumber : ticketNumbers) {

                try {
                    LsStatusResDto res = client.inquiryTicket(ticketNumber);

                    if (res != null) {
                        results.add(res);
                    }

                } catch (Exception e) {
                    log.error("상태조회 실패 ticket={}", ticketNumber, e);
                }
            }

            return results;
        }

        // 티켓 취소
        @Transactional
        public List<LsCancelResDto> cancelTicket(UUID orderId) {

            List<String> ticketNumbers = mapper.selectTicketNumbersByOrderId(orderId);

            if (ticketNumbers == null || ticketNumbers.isEmpty()) {
                throw new RuntimeException("취소 가능한 티켓 없음");
            }

            //  상태 전체 조회
            List<LsStatusResDto> statusList = inquiryTicket(orderId);

            // 사전 검증
            for (LsStatusResDto statusRes : statusList) {

                String status = statusRes.getResultCode();

                if (!"T000".equals(status)) {
                    switch (status) {
                        case "T001":
                            throw new RuntimeException("이미 사용된 티켓 포함 → 취소 불가");
                        case "T002":
                            throw new RuntimeException("취소 진행중 티켓 포함 → 취소 불가");
                        case "T003":
                            throw new RuntimeException("이미 취소된 티켓 포함 → 취소 불가");
                        default:
                            throw new RuntimeException("알 수 없는 상태 → 취소 불가");
                    }
                }
            }

            // 전체 취소
            List<LsCancelResDto> results = new ArrayList<>();

            for (String ticketNumber : ticketNumbers) {

                LsCancelResDto res = client.cancelTicket(ticketNumber);

                if (res == null || !"0000".equals(res.getResultCode())) {
                    throw new RuntimeException("취소 실패 ticket=" + ticketNumber);
                }

                results.add(res);
            }

            return results;
        }


        // LS 티켓 문자 재전송
        public List<LsResendResDto> resendTicket(UUID orderId) {

            LsOrderInfoDto orderInfo = mapper.selectOrderInfoByOrderId(orderId);

            if (orderInfo == null) {
                throw new RuntimeException("주문정보 없음 orderId=" + orderId);
            }

            if ("CANCELLED".equals(orderInfo.getOrderStatus())) {
                throw new RuntimeException("취소된 주문은 재발송 불가");
            }

            String orderNumber = orderInfo.getOrderNumber();

            List<LsResendResDto> results = new ArrayList<>();

            try {
                // 1번만 호출
                LsResendResDto res = client.resendTicket(orderNumber);

                if ("0000".equals(res.getResultCode())) {
                    log.info("문자 재전송 성공 orderNumber={}", orderNumber);
                } else {
                    log.warn("문자 재전송 실패 orderNumber={} code={}",
                            orderNumber,
                            res.getResultCode());
                }

                results.add(res);

            } catch (Exception e) {
                log.error("문자 재전송 실패 orderNumber={}", orderNumber, e);
            }

            return results;
        }

    // 티켓사용처리
    @Transactional
    public LsTicketUseResDto ticketUse(LsTicketUseReqDto req) {
        // 1. 티켓 조회
        LsOrderTicket ticket = ticketMapper.findByTicketCodeLs(req.getTransactionId());

        if (ticket == null) {
            return fail("티켓 없음");
        }

        // 2. 이미 사용됐는데 사용으로 들어오거나 이미 사용취소인데 사용취소로 들어올경우
        if (ticket.isTicketUsed() && req.getCode().equals("use")){
            return fail("이미 사용된 티켓");
        }

        if (!ticket.isTicketUsed() && req.getCode().equals("useCancle")) {
            return fail("이미 취소된 티켓");
        }

        // 3. 사용 처리
        int updated = ticketMapper.useTicketLs(
                req.getTransactionId(),
                req.getCode(),
                req.getDate()
        );

        if (updated == 0) {
            return fail("사용 처리 실패");
        }

        return success();
    }

    private LsTicketUseResDto success() {
        return LsTicketUseResDto.builder()
                .status("success")
                .resultCode("0000")
                .resultMessage("성공")
                .build();
    }

    private LsTicketUseResDto fail(String msg) {
        return LsTicketUseResDto.builder()
                .status("error")
                .resultCode("9999")
                .resultMessage(msg)
                .build();
    }
}






