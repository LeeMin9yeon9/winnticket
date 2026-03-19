package kr.co.winnticket.integration.benepia.order.scheduler;

import kr.co.winnticket.integration.benepia.order.service.BenepiaOrderBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Log4j2
public class BenepiaOrderBatchScheduler {

    private final BenepiaOrderBatchService batchService;
    // 새벽 1시
    @Scheduled(cron = "0 0 1 * * *")
    public void run() {
        LocalDate targetDate = LocalDate.now().minusDays(1);
        batchService.runBatch(targetDate);
    }

}
