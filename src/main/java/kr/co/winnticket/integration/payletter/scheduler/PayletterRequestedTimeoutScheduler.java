package kr.co.winnticket.integration.payletter.scheduler;

import kr.co.winnticket.common.enums.ProductType;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointCancelReqDto;
import kr.co.winnticket.integration.benepia.kcp.service.KcpService;
import kr.co.winnticket.order.admin.dto.OrderItemOptionDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import kr.co.winnticket.ticketCoupon.service.TicketCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayletterRequestedTimeoutScheduler {
    private final OrderShopMapper mapper;
    private final OrderMapper orderMapper;
    private final KcpService kcpService;
    private final TicketCouponService ticketCouponService;


    @Scheduled(fixedDelay = 300000) // 5분
    @Transactional(propagation = REQUIRES_NEW)
    public void expireOrders() {

        log.info("주문 만료/타임아웃 체크 시작");

        List<String> orderNumbers = mapper.findRequestedTimeoutOrders();

        for (String orderNumber : orderNumbers) {

            try {

                int locked = mapper.lockRequestedCanceling(orderNumber);
                if (locked == 0) continue;

                log.info("자동취소 시작 order={}", orderNumber);

                UUID orderId = orderMapper.findOrderIdByOrderNumber(orderNumber);
                if (orderId == null) {
                    log.error("orderId 없음 order={}", orderNumber);
                    mapper.rollbackCanceling(orderNumber);
                    continue;
                }

                /*
                 * 1. 포인트 환불
                 */
                String pointTid = orderMapper.selectPointTno(orderNumber);

                if (pointTid != null && !pointTid.isBlank()) {
                    KcpPointCancelReqDto dto = new KcpPointCancelReqDto();
                    dto.setTno(pointTid);
                    dto.setCancelReason("결제 미완료/입금기한 초과 자동취소");

                    kcpService.cancelPoint(dto);

                    log.info("포인트 환불 완료 order={}", orderNumber);
                }

                /*
                 * 2. 재고 복구
                 */
                List<OrderItemOptionDto> options = orderMapper.selectOrderItemOptions(orderId);

                for (OrderItemOptionDto opt : options) {
                    if (!ProductType.STAY.equals(opt.getProductType())) {
                        orderMapper.increaseStock(
                                opt.getOptionValueId(),
                                opt.getQuantity()
                        );
                    }
                }

                log.info("재고 복구 완료 order={}", orderNumber);

                /*
                 * 3. 예약쿠폰 복구
                 */
                ticketCouponService.restoreReservedCoupons(orderId);

                log.info("쿠폰 복구 완료 order={}", orderNumber);

                /*
                 * 4. 상태 완료
                 */
                mapper.updateExpireCompleted(orderNumber);

                log.info("자동취소 완료 order={}", orderNumber);


            } catch (Exception e) {

                log.error("자동취소 실패 order={}", orderNumber, e);

                try {
                    mapper.rollbackCanceling(orderNumber);
                } catch (Exception rollbackEx) {
                    log.error("롤백 실패 order={}", orderNumber, rollbackEx);
                }
            }
        }
    }
}
