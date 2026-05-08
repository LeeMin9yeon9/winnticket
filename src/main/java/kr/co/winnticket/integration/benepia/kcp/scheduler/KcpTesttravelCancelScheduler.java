package kr.co.winnticket.integration.benepia.kcp.scheduler;

import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.admin.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j2
public class KcpTesttravelCancelScheduler {

    private final OrderMapper orderMapper;
    private final OrderService orderService;

    @Scheduled(cron = "0 0 12 * * *")
    public void cancelTesttravelPayments() {

        log.info("[TESTTRAVEL CANCEL SCHEDULER START]");

        List<UUID> orderIds =
                orderMapper.selectTesttravelOrderIds();

        log.info("취소 대상 건수={}", orderIds.size());

        for (UUID orderId : orderIds) {

            try {

                orderService.cancelOrder(orderId);

                log.info("[TESTTRAVEL CANCEL SUCCESS] orderId={}", orderId);

            } catch (Exception e) {

                log.error("[TESTTRAVEL CANCEL FAIL] orderId={}", orderId, e);
            }
        }

        log.info("[TESTTRAVEL CANCEL SCHEDULER END]");
    }
}