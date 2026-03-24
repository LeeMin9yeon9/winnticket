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
import java.util.List;
import java.util.UUID;
@Component
@RequiredArgsConstructor
@Slf4j
public class PlaystoryScheduler {

    private final TicketMapper mapper;
    private final PlaystoryService playstoryService;

    @Scheduled(fixedDelay = 300000)
    public void playstoryCheckScheduler() {

        log.info("[Playstory Scheduler Start]");

        List<UUID> orderIds = mapper.selectPlaystoryCheckOrders();

        for (UUID orderId : orderIds) {

            try {

                PlaystoryCheckResponse response = playstoryService.check(orderId);

                if (response.getOptList() == null) {
                    continue;
                }

                int updated = 0;

                for (PlaystoryCheckResponse.OptChkResult opt : response.getOptList()) {
                    try {
                        String code = opt.getResultCode();
                        String cpnNo = opt.getCpnNo();

                    if(code.equals("2001")) {
                        int result = mapper.updatePlaystoryTicketUsed(cpnNo, String.valueOf(LocalDate.now()));

                        if (result > 0) {
                            updated++;
                            log.info("ticket used update success cpnNo={}", cpnNo);
                        }
                    }

                    } catch (Exception e) {
                        log.error("[Playstory] update error cpnNo={}",
                                opt.getCpnNo(), e);
                    }
                }
                log.info("[Playstory] used sync finished updated={}",updated);
            } catch (Exception e) {

                log.error("Playstory check fail orderId={}", orderId, e);

            }

        }
    }
}