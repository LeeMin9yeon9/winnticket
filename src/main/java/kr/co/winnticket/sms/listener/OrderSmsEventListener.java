package kr.co.winnticket.sms.listener;

import kr.co.winnticket.order.admin.service.OrderPaymentCompletedEvent;
import kr.co.winnticket.sms.service.BizMsgService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrderSmsEventListener {

    private final BizMsgService bizMsgService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderPaymentCompletedEvent event) {

        bizMsgService.sendPaymentCompletedSms(
                event.getCustomerPhone(),
                event.getCustomerName(),
                event.getOrderNumber()
        );
    }
}
