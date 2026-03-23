package kr.co.winnticket.order.admin.scheduler;

import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointCancelReqDto;
import kr.co.winnticket.integration.benepia.kcp.service.KcpService;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExpireScheduler {

    private final OrderShopMapper mapper;
    private final OrderMapper orderMapper;
    private final KcpService kcpService;

    // 5분마다 실행
    @Scheduled(fixedDelay = 30000) // 300000
    public void expireOrders() {

        log.info("입금기한 체크 시작");

        // READY + deadline 지난 주문
        List<String> orderNumbers = mapper.findExpiredOrderNumbers();

        for (String orderNumber : orderNumbers) {

            try {

                log.info("입금기한 초과 order={}", orderNumber);

                // 포인트 환불
                String tno = orderMapper.selectPointTno(orderNumber);

                if (tno != null) {

                    KcpPointCancelReqDto dto = new KcpPointCancelReqDto();
                    dto.setTno(tno);
                    dto.setCancelReason("입금기한 초과 자동취소");

                    kcpService.cancelPoint(dto);

                    log.info("포인트 환불 완료 order={}", orderNumber);
                }

                // 주문 실패 처리
                mapper.updatePaymentFailed(orderNumber);

            } catch (Exception e) {
                log.error("자동취소 실패 order={}", orderNumber, e);
            }
        }
    }
}
