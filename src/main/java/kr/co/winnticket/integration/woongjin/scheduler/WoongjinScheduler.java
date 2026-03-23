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

    //@Scheduled(cron = "0 */5 * * * *")
    public void woongjinCheckScheduler() {

        log.info("[Woongjin Scheduler Start]");

        List<String> orderNumbers = mapper.selectWoongjinOrders();

        for (String orderNumber : orderNumbers) {

            try {

                WJOrderInquiryResponse response =
                        woongjinService.inquiry(orderNumber).getData();

                if (response == null || response.getData() == null) {
                    continue;
                }

                for (WJOrderInquiryResponse.DataBlock order : response.getData()) {

                    for (WJOrderInquiryResponse.Product product : order.getProducts()) {

                        String ticketNo = product.getProduct_channel_order_number();

                        // 사용완료
                        if (Boolean.TRUE.equals(product.getIs_used()) || "COMPLETE".equals(product.getProduct_state())) {

                            mapper.updateWoongjinTicketUsed(ticketNo);

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