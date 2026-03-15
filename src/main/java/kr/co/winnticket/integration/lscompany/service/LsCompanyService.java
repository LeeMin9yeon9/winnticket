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
import java.util.ArrayList;
import java.util.List;

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
    public LsIssueResDto issueTicket(String orderNumber) {

        // 주문 정보 조회
        LsOrderInfoDto orderInfo = mapper.selectOrderInfo(orderNumber);

        if (orderInfo == null) {
            throw new RuntimeException("주문정보 없음 orderNumber=" + orderNumber);
        }

        // 주문 상품 조회
        List<LsOrderItemInfoDto> items =
                mapper.selectOrderItemInfos(orderInfo.getOrderId());

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
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        data.setDate(now);

        // 윈앤티켓 주문번호
        data.setOrderNo(orderNumber);

        List<LsIssueReqDto.Data.Order> orderList = new ArrayList<>();


        // 쿠폰 수량만큼 발권 요청 생성
            for (LsOrderItemInfoDto item : items) {
                // 발권 안된 티켓 조회
                List<String> ticketNumbers = mapper.selectOrderTicketNumbers(item.getOrderItemId());

                if (ticketNumbers == null || ticketNumbers.isEmpty()) {
                    log.info("이미 발권 완료된 상품 orderItemId={}", item.getOrderItemId());
                    continue;
                }
                int issueCount = Math.min(item.getQuantity(), ticketNumbers.size());

                for (int i = 0; i < issueCount; i++) {

                    String ticketNumber = ticketNumbers.get(i);

                    LsIssueReqDto.Data.Order o = new LsIssueReqDto.Data.Order();

                    o.setTransactionId(ticketNumber);         // transactionId = 윈앤티켓 티켓번호
                    o.setOptionId(item.getOptionId());      // LS 옵션ID
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

            if (!"0000".equals(res.getResultCode())&& !"E104".equals(res.getResultCode())) {
                log.error("LS 발권 실패 resultCode={} message={}",
                        res.getResultCode(),
                        res.getResultMessage());

                throw new RuntimeException("LS 발권 실패 : " + res.getResultMessage());
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
        public LsStatusResDto inquiryTicket(String ticketNumber){

            String transactionId = ticketNumber;

            return client.inquiryTicket(transactionId);

        }

        // 티켓 취소
        public List<LsCancelResDto> cancelTicket(String orderNumber) {

            // 주문에 해당하는 티켓번호 조회
            List<String> ticketNumbers = mapper.selectTicketNumbersByOrderNumber(orderNumber);
            List<LsCancelResDto> results = new ArrayList<>();

            if (ticketNumbers == null || ticketNumbers.isEmpty()) {
                log.info("LS 취소 대상 없음 orderNumber={}", orderNumber);
                throw new RuntimeException("취소 가능한 LS 티켓이 없습니다.");
            }

            for (String ticketNumber : ticketNumbers) {
                try {
                    // LS 상태조회
                    LsStatusResDto statusRes = client.inquiryTicket(ticketNumber);

                    if (statusRes == null) {
                        log.warn("LS 상태조회 응답 없음 ticketNumber={}", ticketNumber);
                        continue;
                    }

                    // 미사용 상태(T000)만 취소
                    if (statusRes == null || !"T000".equals(statusRes.getResultCode())) {
                        log.info("LS 취소 제외 ticketNumber={}, resultCode={}, message={}",
                                ticketNumber,
                                statusRes.getResultCode(),
                                statusRes.getResultMessage());
                        continue;
                    }

                    LsCancelResDto res = client.cancelTicket(ticketNumber);

                    log.info("LS 취소 응답 ticketNumber={} res={}", ticketNumber, res);

                    results.add(res);

                } catch (Exception e) {
                    log.error("LS 취소 실패 ticketNumber={}", ticketNumber, e);
                }
            }

            if (results.isEmpty()) {
                throw new RuntimeException("취소 가능한 LS 티켓이 없습니다.");
            }

            return results;
        }


        // LS 티켓 문자 재전송
        public LsResendResDto resendTicket(String orderNumber){

            return client.resendTicket(orderNumber);

        }
    }






