package kr.co.winnticket.integration.benepia.kcp.scheduler;

import kr.co.winnticket.integration.benepia.kcp.dto.KcpCancelTargetDto;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointCancelReqDto;
import kr.co.winnticket.integration.benepia.kcp.service.KcpService;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class KcpTesttravelCancelScheduler {
    private final OrderMapper orderMapper;
    private final OrderShopMapper orderShopMapper;
    private final KcpService kcpService;

    /**
     * 매일 밤 11시 실행
     */
    @Scheduled(cron = "0 0 11 * * *")
    @Transactional
    public void cancelTesttravelPayments() {

        log.info("[KCP TESTTRAVEL CANCEL SCHEDULER START]");

        try {

            // testtravel 결제건 조회
            List<KcpCancelTargetDto> targets =
                    orderMapper.selectTesttravelPaymentTargets();

            log.info("취소 대상 건수 : {}", targets.size());

            for (KcpCancelTargetDto target : targets) {

                try {

                    log.info(
                            "[KCP CANCEL TRY] orderNo={}, tno={}",
                            target.getOrderNo(),
                            target.getTno()
                    );

                    KcpPointCancelReqDto req = new KcpPointCancelReqDto();

                    req.setTno(target.getTno());
                    req.setModType("STSC");
                    req.setCancelReason("TEST 계정 자동 취소");

                    // 전체취소
                    req.setModType("STSC");

                    kcpService.cancelPoint(req);

                    // 상태만 최소 변경
                    orderMapper.updateTesttravelCancelStatus(
                            target.getOrderNo()
                    );

                    log.info(
                            "[KCP CANCEL SUCCESS] orderNo={}",
                            target.getOrderNo()
                    );

                } catch (Exception e) {

                    log.error(
                            "[KCP CANCEL FAIL] orderNo={}",
                            target.getOrderNo(),
                            e
                    );
                }
            }

        } catch (Exception e) {

            log.error("[KCP TESTTRAVEL CANCEL SCHEDULER ERROR]", e);

        }

        log.info("[KCP TESTTRAVEL CANCEL SCHEDULER END]");
    }
}
