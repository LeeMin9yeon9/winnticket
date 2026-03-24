package kr.co.winnticket.integration.spavis.scheduler;

import jakarta.transaction.Transactional;
import kr.co.winnticket.integration.spavis.client.SpavisClient;
import kr.co.winnticket.integration.spavis.dto.SPCouponCheckResponse;
import kr.co.winnticket.integration.spavis.mapper.SpavisMapper;
import kr.co.winnticket.ticket.mapper.TicketMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpavisScheduler {

    private final SpavisMapper spavisMapper;
    private final TicketMapper ticketMapper;
    private final SpavisClient spavisClient;

    //@Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void syncSpavisUsed() {

        log.info("[Spavis] scheduler start");

        List<UUID> orderIds =
                ticketMapper.findUnusedSpavisCoupons();

        for (UUID orderId : orderIds) {

            List<String> couponNos = Collections.singletonList(spavisMapper.selectCouponNo(orderId));

            for (String couponNo : couponNos) {

                try {
                    SPCouponCheckResponse res = spavisClient.checkCoupon(couponNo);

                    if (res == null) continue;

                    if (!"S".equals(res.getRtnDiv())) continue;

                    if (res.getCoupons() == null) continue;

                    for (SPCouponCheckResponse.RtnCoupon coupon : res.getCoupons()) {

                        // 사용된 쿠폰
                        if ("I".equals(coupon.getStatusDiv())) {

                            ticketMapper.updateSpavisTicketUsed(
                                    coupon.getCouponNo(),
                                    coupon.getResultDate()
                            );

                            log.info("[Spavis] used coupon={}", coupon.getCouponNo());
                        }
                    }

                } catch (Exception e) {
                    log.error("[Spavis] error couponNo={}", couponNo, e);

                }
            }
        }

        log.info("[Spavis] scheduler end");

    }
}