package kr.co.winnticket.integration.benepia.batch.scheduler;

import kr.co.winnticket.integration.benepia.batch.service.BenepiaBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BenepiaBatchScheduler {

    private final BenepiaBatchService batchService;

    /**
     * 매일 새벽 6시 실행 (cron: 초 분 시 일 월 요일)
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void runTicketBatch() {
        log.info("[BATCH] 베네피아 티켓 배치 스케줄 시작");
        try {
            batchService.executeTicketBatch();
            log.info("[BATCH] 베네피아 티켓 배치 스케줄 정상 종료");
        } catch (Exception e) {
            log.error("[BATCH] 베네피아 티켓 배치 실행 중 오류", e);
            // 필요하면 여기서 알림(슬랙/이메일) 연동
        }
    }
}
