package kr.co.winnticket.integration.mair.scheduler;

import kr.co.winnticket.integration.mair.mapper.MairOrderMapper;
import kr.co.winnticket.integration.mair.service.MairService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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

        List<Map<String, String>> tickets = mapper.selectPendingTickets();

        log.info("[MAIR-SCHEDULER] target tickets size={}", tickets.size());

        for (Map<String, String> t : tickets) {

            String trno = t.get("trno");
            String itcd = t.get("productcode");

            try {
                mairService.useCheckSingle(trno, itcd);

            } catch (Exception e) {
                log.error("[MAIR-SCHEDULER] fail TRNO={}", trno, e);
            }
        }

        log.info("[MAIR-SCHEDULER] END");
    }
}
