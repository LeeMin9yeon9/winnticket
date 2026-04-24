package kr.co.winnticket.integration.woongjin.scheduler;

import kr.co.winnticket.integration.woongjin.dto.WJOrderInquiryResponse;
import kr.co.winnticket.integration.woongjin.service.WoongjinService;
import kr.co.winnticket.integration.woongjin.mapper.WoongjinMapper;
import kr.co.winnticket.ticket.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
@Slf4j
public class WoongjinScheduler {

    private final WoongjinService woongjinService;
    private final TicketMapper mapper;

    //@Scheduled(fixedDelay = 3600000)
    public void woongjinCheckScheduler() {

        log.info("[Woongjin Scheduler Start]");

        List<String> orderNumbers = mapper.selectWoongjinOrders();
        log.info("[Woongjin Scheduler] DB 조회 결과 - 대상 주문 수: {}건, 주문번호 목록: {}",
                orderNumbers.size(), orderNumbers);

        if (orderNumbers.isEmpty()) {
            log.info("[Woongjin Scheduler] 처리할 대상이 없어 종료합니다.");
            return;
        }

        for (String orderNumber : orderNumbers) {

            try {
                log.info("[Woongjin] API 호출 시작 - orderNumber: {}", orderNumber);

                var apiResponse = woongjinService.inquiry(orderNumber);

                if (apiResponse == null || apiResponse.getData() == null) {
                    log.warn("[Woongjin] API 응답 객체 자체가 null입니다. orderNumber: {}", orderNumber);
                    continue;
                }

                WJOrderInquiryResponse response = apiResponse.getData();

                if (response.getData() == null) {
                    log.error("[Woongjin] DTO 파싱 실패 혹은 리스트가 null입니다. DTO 구조를 확인하세요. orderNumber: {}", orderNumber);
                    continue;
                }

                log.info("[Woongjin] API 응답 분석 - 포함된 주문(DataBlock) 수: {}건", response.getData().size());

                for (WJOrderInquiryResponse.DataBlock order : response.getData()) {
                    log.info("[Woongjin] 주문정보 확인 - channel_order_number: {}, 포함된 상품 수: {}건",
                            order.getChannel_order_number(),
                            (order.getProducts() != null ? order.getProducts().size() : 0));
                    for (WJOrderInquiryResponse.Product product : order.getProducts()) {
                        String pin = product.getPin();
                        String ticketNo = product.getProduct_channel_order_number();
                        String usedAt = product.getUsed_at();
                        String formattedUsedAt = usedAt.replaceAll("[^0-9]", "").substring(0, 14);
                        log.info("[Woongjin] 상품 상태 확인 - Pin: {}, State: {}, IsUsed: {}",
                                pin, product.getProduct_state(), product.getIs_used());
                        // 사용완료
                        if (Boolean.TRUE.equals(product.getIs_used()) || "COMPLETE".equals(product.getProduct_state())) {

                            int updatedRows = mapper.updateWoongjinTicketUsed(ticketNo, formattedUsedAt);

                            if (updatedRows > 0) {
                                log.info("[Woongjin] ★사용 처리 성공★ - ticketNo(pin): {}", pin);
                            } else {
                                log.warn("[Woongjin] 사용 조건은 맞으나 DB 업데이트 실패 (해당 pin이 DB에 없음) - pin: {}, 채널티켓번호: {}",
                                        pin, ticketNo);
                            }
                            log.info("Woongjin ticket used ticketNo={}", ticketNo);

                        }
                    }

                }

            } catch (Exception e) {

                log.error("Woongjin check fail orderNumber={}", orderNumber, e);

            }

        }

        log.info("[Woongjin Scheduler End]");
    }
}