package kr.co.winnticket.order.admin.service;

import java.util.UUID;

/**
 * 결제 완료 처리(completePayment) 도중 외부 결제(카드/포인트)는 이미 일어났으나
 * 후속 처리(포인트 차감 / 티켓 발행 / 파트너 발권 등)가 실패해 트랜잭션을 롤백해야 하는 경우 던진다.
 *
 * <p>이 예외가 던져지면 호출 트랜잭션은 롤백되며(잠금 해제), 호출측(컨트롤러)은
 * 트랜잭션이 완전히 롤백된 뒤 {@code compensateFailedPayment}를 호출해
 * 실제로 빠져나간 카드/포인트를 환불(보상)한다.
 *
 * <p>롤백되면 DB의 point_tid가 사라져 포인트 tno를 다시 조회할 수 없으므로,
 * 차감 시점에 확보한 KCP tno를 예외에 담아 호출측으로 전달한다.
 */
public class PaymentCompensationException extends RuntimeException {

    private final UUID orderId;
    private final String kcpTno; // 혼합결제에서 차감된 포인트 tno (없으면 null)

    public PaymentCompensationException(UUID orderId, String kcpTno, String message) {
        super(message);
        this.orderId = orderId;
        this.kcpTno = kcpTno;
    }

    public PaymentCompensationException(UUID orderId, String kcpTno, String message, Throwable cause) {
        super(message, cause);
        this.orderId = orderId;
        this.kcpTno = kcpTno;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getKcpTno() {
        return kcpTno;
    }
}
