package kr.co.winnticket.integration.lscompany.service;

import kr.co.winnticket.common.enums.OptionCode;
import kr.co.winnticket.integration.lscompany.client.LsCompanyClient;
import kr.co.winnticket.integration.lscompany.dto.*;
import kr.co.winnticket.integration.lscompany.mapper.LsCompanyMapper;
import kr.co.winnticket.integration.lscompany.props.LsCompanyProperties;
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

    // 시설 조회
    public LsPlaceResDto getPlaces() {

        return client.getPlaces();
    }

    // 상품 조회
//    public LsProductResDto getProducts(){
//
//        return client.getProducts();
//    }
    public LsProductResDto getProducts(String productCode){
        return client.getProducts(productCode);
    }

    // 티켓 발권
    @Transactional
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
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

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

                int issueCount = ticketNumbers.size();

                for (int i = 0; i < issueCount; i++) {

                    String ticketNumber = ticketNumbers.get(i);

                    LsIssueReqDto.Data.Order o = new LsIssueReqDto.Data.Order();

                    o.setTransactionId(ticketNumber);         // transactionId = 윈앤티켓 티켓번호
                    o.setOptionId(item.getOptionId());      // LS 옵션ID
                    o.setPrice(String.valueOf(item.getPrice()));    // 판매가
                    o.setDiscount(item.getDiscount() == null ? "0" : String.valueOf(item.getDiscount()));   // 할인금액

                    LsOptionMapping mapping = mapToLsOption(item.getOptionCode());

                    o.setOptionType(mapping.optionType);
                    o.setOptionName(mapping.optionName);

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
        public List<LsCancelResDto> cancelTicket(UUID orderId) {

            List<String> ticketNumbers = mapper.selectTicketNumbersByOrderId(orderId);

            List<LsCancelResDto> results = new ArrayList<>();

            if (ticketNumbers == null || ticketNumbers.isEmpty()) {
                log.info("LS 취소 대상 없음 orderId={}", orderId);
                throw new RuntimeException("취소 가능한 LS 티켓이 없습니다.");
            }

            for (String ticketNumber : ticketNumbers) {
                try {
                    // 상태조회
                    LsStatusResDto statusRes = client.inquiryTicket(ticketNumber);

                    if (statusRes == null) {
                        log.warn("LS 상태조회 응답 없음 ticketNumber={}", ticketNumber);
                        continue;
                    }

                    // T000만 취소 가능
                    if (!"T000".equals(statusRes.getResultCode())) {
                        log.info("LS 취소 제외 ticketNumber={}, status={}",
                                ticketNumber,
                                statusRes.getResultCode());
                        continue;
                    }

                    String status = statusRes.getResultCode();

                    switch (status) {
                        case "T001":
                            log.info("이미 사용된 티켓");
                            break;
                        case "T002":
                            log.info("취소 진행중");
                            break;
                        case "T003":
                            log.info("이미 취소됨");
                            break;
                    }

                    // 취소 요청
                    LsCancelResDto res = client.cancelTicket(ticketNumber);
                    String code = res.getResultCode();

                    // 성공
                    if ("0000".equals(code)) {
                        log.info("LS 취소 성공 ticketNumber={}", ticketNumber);
                        results.add(res);
                        continue;
                    }

                    // 이미 취소됨
                    if ("E209".equals(code)) {
                        log.info("이미 취소된 티켓 ticketNumber={}", ticketNumber);
                        continue;
                    }

                    // 취소 불가
                    if ("E207".equals(code)) {
                        log.warn("취소 불가 티켓 ticketNumber={}", ticketNumber);
                        continue;
                    }

                    // 기타 에러
                    throw new RuntimeException("LS 취소 실패 ticket=" + ticketNumber + " code=" + code);


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
        public List<LsResendResDto> resendTicket(UUID orderId) {

            List<String> ticketNumbers = mapper.selectTicketNumbersByOrderId(orderId);

            List<LsResendResDto> results = new ArrayList<>();

            for (String ticketNumber : ticketNumbers) {
                try {
                    LsResendResDto res = client.resendTicket(ticketNumber);

                    if ("0000".equals(res.getResultCode())) {
                        log.info("문자 재전송 성공 ticketNumber={}", ticketNumber);
                    } else {
                        log.warn("문자 재전송 실패 ticketNumber={} code={}",
                                ticketNumber,
                                res.getResultCode());
                    }

                    results.add(res);

                } catch (Exception e) {
                    log.error("문자 재전송 실패 ticketNumber={}", ticketNumber, e);
                }
            }

            return results;
        }

    // 내부 static class
    private static class LsOptionMapping {
        String optionType;
        String optionName;

        public LsOptionMapping(String optionType, String optionName) {
            this.optionType = optionType;
            this.optionName = optionName;
        }
    }

    // 매핑 메서드
    private LsOptionMapping mapToLsOption(OptionCode optionCode) {
        if (optionCode == null) {
            throw new IllegalArgumentException("OptionCode 없음");
        }

        return switch (optionCode) {
            case MOBILE -> new LsOptionMapping("PRT1", "단품");
            case MOBILE_MANUAL -> new LsOptionMapping("PRT1", "단품");
            case OPTION -> new LsOptionMapping("PRT2", "패키지");
        };
    }
    }






