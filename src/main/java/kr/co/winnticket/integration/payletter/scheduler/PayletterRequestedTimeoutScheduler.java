package kr.co.winnticket.integration.payletter.scheduler;

import kr.co.winnticket.common.lock.SchedulerLock;
import kr.co.winnticket.order.admin.dto.OrderAdminDetailGetResDto;
import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.admin.service.OrderCleanupService;
import kr.co.winnticket.order.admin.service.OrderPostPaymentService;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
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
public class PayletterRequestedTimeoutScheduler {
    private final OrderShopMapper mapper;
    private final OrderMapper orderMapper;
    private final OrderCleanupService orderCleanupService;
    private final OrderPostPaymentService orderPostPaymentService;
    private final SchedulerLock schedulerLock;


    @Scheduled(fixedDelay = 300000) // 5분
    @Transactional(propagation = REQUIRES_NEW)
    public void expireOrders() {

        // 다중 인스턴스 환경 동시 실행 방지 (TTL 락 1회 실행시간 + 여유)
        String lockToken = schedulerLock.acquire("payletter-expire-orders", Duration.ofMinutes(4));
        if (lockToken == null) return;

        try {
            log.info("주문 만료/타임아웃 체크 시작");

            doExpire();
        } finally {
            schedulerLock.release("payletter-expire-orders", lockToken);
        }
    }

    private void doExpire() {
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

                // 통합 정리 (포인트→카드→재고→쿠폰)
                orderCleanupService.refundPointIfCharged(orderId, "결제 미완료/입금기한 초과 자동취소");
                orderCleanupService.refundCardWithoutFee(orderId);
                orderCleanupService.restoreStock(orderId);
                orderCleanupService.restoreCoupons(orderId);

                // 상태 완료 (CANCELING → FAILED + CANCELED)
                mapper.updateExpireCompleted(orderNumber);
                log.info("자동취소 완료 order={}", orderNumber);

                // 취소 안내 SMS (트랜잭션 커밋 후 발송)
                final String finalOrderNumber = orderNumber;
                OrderAdminDetailGetResDto order = orderMapper.selectOrderAdminDetail(orderId);
                List<OrderProductListGetResDto> items = orderMapper.selectOrderProductList(orderId);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            orderPostPaymentService.sendOrderCancelledSms(order, items);
                            log.info("자동취소 문자 발송 완료 order={}", finalOrderNumber);
                        } catch (Exception e) {
                            log.error("자동취소 문자 실패 order={}", finalOrderNumber, e);
                        }
                    }
                });


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
