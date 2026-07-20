package kr.co.winnticket.integration.payletter.scheduler;

import kr.co.winnticket.common.lock.SchedulerLock;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.admin.service.OrderCleanupService;
import kr.co.winnticket.order.admin.service.OrderPostPaymentService;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayletterRequestedTimeoutScheduler {
    private final OrderShopMapper mapper;
    private final OrderMapper orderMapper;
    private final OrderCleanupService orderCleanupService;
    private final OrderPostPaymentService orderPostPaymentService;
    private final SchedulerLock schedulerLock;
    private final PlatformTransactionManager txManager;


    @Scheduled(fixedDelay = 300000) // 5분
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

        // 주문 1건씩 독립 트랜잭션(REQUIRES_NEW)으로 처리한다.
        // 한 건의 토스 취소 실패(@Transactional cancel())이 배치 트랜잭션 전체를 rollback-only로
        // 오염시켜(UnexpectedRollbackException) 다른 주문 정리까지 통째로 롤백되던 문제를 격리한다.
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        for (String orderNumber : orderNumbers) {
            try {
                txTemplate.executeWithoutResult(status -> expireOneOrder(orderNumber));
            } catch (Exception e) {
                // 이 주문만 롤백됨. 다른 주문은 이미 각자 트랜잭션으로 커밋되어 영향 없음.
                log.error("자동취소 실패 order={}", orderNumber, e);

                // CANCELING 상태 원복 (위 트랜잭션은 롤백되어 닫혔으므로 별도 트랜잭션에서 수행)
                try {
                    TransactionTemplate rollbackTemplate = new TransactionTemplate(txManager);
                    rollbackTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                    rollbackTemplate.executeWithoutResult(status -> mapper.rollbackCanceling(orderNumber));
                } catch (Exception rollbackEx) {
                    log.error("롤백 실패 order={}", orderNumber, rollbackEx);
                }
            }
        }
    }

    /**
     * 주문 1건 자동취소 (독립 트랜잭션 내부에서 실행).
     * 예외를 던지면 이 주문의 트랜잭션만 롤백되고 다음 주문에는 영향이 없다.
     */
    private void expireOneOrder(String orderNumber) {
        int locked = mapper.lockRequestedCanceling(orderNumber);
        if (locked == 0) return;

        log.info("자동취소 시작 order={}", orderNumber);

        UUID orderId = orderMapper.findOrderIdByOrderNumber(orderNumber);
        if (orderId == null) {
            // 던지면 이 주문 트랜잭션 롤백 → 바깥 catch에서 rollbackCanceling 처리
            throw new IllegalStateException("orderId 없음 order=" + orderNumber);
        }

        // 통합 정리 (포인트→카드→재고→쿠폰)
        orderCleanupService.refundPointIfCharged(orderId, "결제 미완료/입금기한 초과 자동취소");
        orderCleanupService.refundCardWithoutFee(orderId);
        orderCleanupService.restoreStock(orderId);
        orderCleanupService.restoreCoupons(orderId);

        // 상태 완료 (CANCELING → FAILED + CANCELED)
        mapper.updateExpireCompleted(orderNumber);
        log.info("자동취소 완료 order={}", orderNumber);
    }
}
