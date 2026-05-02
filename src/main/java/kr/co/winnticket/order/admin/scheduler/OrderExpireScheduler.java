package kr.co.winnticket.order.admin.scheduler;

import kr.co.winnticket.common.enums.ProductType;
import kr.co.winnticket.common.lock.SchedulerLock;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointCancelReqDto;
import kr.co.winnticket.integration.benepia.kcp.service.KcpService;
import kr.co.winnticket.order.admin.dto.OrderAdminDetailGetResDto;
import kr.co.winnticket.order.admin.dto.OrderItemOptionDto;
import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.admin.service.OrderPostPaymentService;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import kr.co.winnticket.ticketCoupon.service.TicketCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExpireScheduler {

    private final OrderShopMapper mapper;
    private final OrderMapper orderMapper;
    private final KcpService kcpService;
    private final TicketCouponService ticketCouponService;
    private final OrderPostPaymentService orderPostPaymentService;
    private final SchedulerLock schedulerLock;

    // 5분마다 실행
    @Scheduled(fixedDelay = 300000)
    @Transactional(propagation = REQUIRES_NEW)
    public void expireOrders() {

        // 다중 인스턴스 동시 실행 방지
        String lockToken = schedulerLock.acquire("order-expire", Duration.ofMinutes(4));
        if (lockToken == null) return;

        try {
            log.info("입금기한 체크 시작");
            doExpire();
        } finally {
            schedulerLock.release("order-expire", lockToken);
        }
    }

    private void doExpire() {
        // READY + deadline 지난 주문
        List<String> orderNumbers = mapper.findExpiredOrderNumbers();

        for (String orderNumber : orderNumbers) {

            try {

                //중복 방지
                int locked = mapper.updateToCanceling(orderNumber);
                if (locked == 0) {
                    continue; // 이미 처리중
                }

                log.info("입금기한 초과 처리 시작 order={}", orderNumber);

                // 포인트 환불
                String tno = orderMapper.selectPointTno(orderNumber);

                if (tno != null && !tno.isBlank()){

                    KcpPointCancelReqDto dto = new KcpPointCancelReqDto();
                    dto.setTno(tno);
                    dto.setCancelReason("입금기한 초과 자동취소");

                    kcpService.cancelPoint(dto);

                    log.info("포인트 환불 완료 order={}", orderNumber);
                }

                // 재고 + 예약 쿠폰 복구 (orderId 필요)
                UUID orderId = orderMapper.findOrderIdByOrderNumber(orderNumber);

                if (orderId == null) {
                    log.error("orderId 없음 orderNumber={}", orderNumber);
                    continue;
                }

                    // 재고 복구
                    List<OrderItemOptionDto> options = orderMapper.selectOrderItemOptions(orderId);
                    for (OrderItemOptionDto opt : options) {
                        if (!ProductType.STAY.equals(opt.getProductType())) {
                            orderMapper.increaseStock(opt.getOptionValueId(), opt.getQuantity());
                        }
                    }
                    log.info("재고 복구 완료 order={}", orderNumber);

                    // 선사입 예약 쿠폰 복구
                    ticketCouponService.restoreReservedCoupons(orderId);
                    log.info("쿠폰 복구 완료 order={}", orderNumber);

                // KCP 주문 실패 처리
                mapper.updatePaymentFailed(orderNumber);
                //  최종 상태 (결제 + 주문 상태 둘 다 변경)
                mapper.updateExpireCompleted(orderNumber);

                log.info("자동취소 완료 order={}", orderNumber);

                // 문자발송
                OrderAdminDetailGetResDto order = orderMapper.selectOrderAdminDetail(orderId);
                List<OrderProductListGetResDto> items = orderMapper.selectOrderProductList(orderId);

                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                            @Override
                            public void afterCommit() {
                                try {
                                    orderPostPaymentService.sendOrderCancelledSms(order, items);
                                    log.info("자동취소 문자 발송 완료 orderId={}", orderId);
                                } catch (Exception e) {
                                    log.error("자동취소 문자 실패 orderId={}", orderId, e);
                                }
                            }
                        }
                );

            } catch (Exception e) {
                log.error("자동취소 실패 order={}", orderNumber, e);
                // 실패 시 상태 롤백 (재시도 가능하게)
                try {
                    mapper.rollbackCanceling(orderNumber);
                } catch (Exception rollbackEx) {
                    log.error("상태 롤백 실패 order={}", orderNumber, rollbackEx);
                }
            }
        }
    }
}
