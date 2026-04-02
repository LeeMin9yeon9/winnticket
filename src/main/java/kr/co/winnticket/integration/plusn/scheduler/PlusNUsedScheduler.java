package kr.co.winnticket.integration.plusn.scheduler;

import kr.co.winnticket.integration.plusn.client.PlusNClient;
import kr.co.winnticket.integration.plusn.dto.PlusNUsedDateRequest;
import kr.co.winnticket.integration.plusn.dto.PlusNUsedDateResponse;
import kr.co.winnticket.integration.plusn.dto.PlusNUsedDateResponse.Coupon;
import kr.co.winnticket.integration.plusn.dto.PlusNUsedDateResponse.UsedOrder;
import kr.co.winnticket.ticket.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlusNUsedScheduler {

    private final PlusNClient plusNClient;
    private final TicketMapper ticketMapper;

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.BASIC_ISO_DATE;


    // 1시간마다 오늘 사용 조회
    @Scheduled(fixedDelay = 60000)
    public void syncToday() {
        sync(LocalDate.now());
    }

    // 실제 동기화 로직
    private void sync(LocalDate date) {

        String orderDate = date.format(FORMAT);

        log.info("[PlusN] used sync start date={}", orderDate);

        try {
            PlusNUsedDateRequest req = new PlusNUsedDateRequest();
            req.setOrder_date(orderDate);

            PlusNUsedDateResponse res = plusNClient.usedDate(req);

            if (res == null || res.getUsed_list() == null) {
                log.info("[PlusN] used sync empty date={}", orderDate);
                return;
            }

            int updated = 0;

            for (UsedOrder order : res.getUsed_list()) {

                if (order.getCoupon() == null) continue;

                for (Coupon coupon : order.getCoupon()) {
                    try {

                        int result = ticketMapper.updateTicketUsed(
                                coupon.getOrder_sales(),
                                coupon.getResult_date()
                        );

                        if (result > 0) {
                            updated++;
                        }

                    } catch (Exception e) {

                        log.error("[PlusN] update error orderSales={}",
                                coupon.getOrder_sales(), e);

                    }
                }
            }

            log.info("[PlusN] used sync finished date={} updated={}",
                    orderDate, updated);

        } catch (Exception e) {

            log.error("[PlusN] used sync error date={}", orderDate, e);

        }
    }
}