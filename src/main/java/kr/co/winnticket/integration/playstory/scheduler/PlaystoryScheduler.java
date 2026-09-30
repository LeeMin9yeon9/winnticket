package kr.co.winnticket.integration.playstory.scheduler;

import kr.co.winnticket.integration.playstory.dto.PlaystoryCheckResponse;
import kr.co.winnticket.integration.playstory.service.PlaystoryService;
import kr.co.winnticket.integration.playstory.mapper.PlaystoryMapper;
import kr.co.winnticket.ticket.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
@Component
@RequiredArgsConstructor
@Slf4j
public class PlaystoryScheduler {

    private final TicketMapper mapper;
    private final PlaystoryService playstoryService;

    @Scheduled(fixedDelay = 3600000)
    public void playstoryCheckScheduler() {

        List<UUID> orderIds = mapper.selectPlaystoryCheckOrders();

        // 대상이 없으면 로그조차 남기지 않음(매시간 빈 실행까지 남길 필요 없음)
        if (orderIds.isEmpty()) {
            return;
        }

        int totalUpdated = 0;

        for (UUID orderId : orderIds) {

            try {

                PlaystoryCheckResponse response = playstoryService.check(orderId);

                if (response.getOptList() == null) {
                    continue;
                }

                for (PlaystoryCheckResponse.OptChkResult opt : response.getOptList()) {
                    try {
                        String code = opt.getResultCode();
                        String cpnNo = opt.getCpnNo();

                    if(code.equals("2001")) {
                        String now = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "000000";
                        int result = mapper.updatePlaystoryTicketUsed(cpnNo, now);

                        if (result > 0) {
                            totalUpdated++;
                            log.info("ticket used update success cpnNo={}", cpnNo);
                        }
                    }

                    } catch (Exception e) {
                        log.error("[Playstory] update error cpnNo={}",
                                opt.getCpnNo(), e);
                    }
                }
            } catch (Exception e) {

                log.error("Playstory check fail orderId={}", orderId, e);

            }

        }

        // 주문 건별 로그 대신, 전체 실행 결과만 한 줄로 요약해서 로그 스팸 방지
        log.info("[Playstory Scheduler] checked={}, updated={}", orderIds.size(), totalUpdated);
    }
}