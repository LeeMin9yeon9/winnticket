package kr.co.winnticket.integration.mair.scheduler;

import kr.co.winnticket.integration.mair.mapper.MairOrderMapper;
import kr.co.winnticket.integration.mair.service.MairService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MairScheduler {

    private final MairOrderMapper mapper;
    private final MairService mairService;

    // 5분마다
    @Scheduled(fixedDelay = 300000)
    public void syncMairTicketUsage() {

        log.info("[MAIR-SCHEDULER] START");

        List<String> orderNumbers = mapper.selectMairPendingOrders();

        for (String orderNumber : orderNumbers) {

            try {
                log.info("[MAIR-SCHEDULER] checking order={}", orderNumber);

                mairService.useCheckByOrderNumber(orderNumber);

            } catch (Exception e) {
                log.error("[MAIR-SCHEDULER] fail order={}", orderNumber, e);
            }
        }

        log.info("[MAIR-SCHEDULER] END");
    }
}
