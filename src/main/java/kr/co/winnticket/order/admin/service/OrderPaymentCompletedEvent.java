package kr.co.winnticket.order.admin.service;

import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderPaymentCompletedEvent {
    private final UUID orderId;
    private final String customerPhone;
    private final String orderNumber;
    private final String customerName;

    public OrderPaymentCompletedEvent(UUID orderId,
                                      String customerPhone,
                                      String customerName,
                                      String orderNumber) {
        this.orderId = orderId;
        this.customerPhone = customerPhone;
        this.customerName = customerName;
        this.orderNumber = orderNumber;
    }
}
