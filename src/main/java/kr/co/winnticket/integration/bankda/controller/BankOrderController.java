package kr.co.winnticket.integration.bankda.controller;

import kr.co.winnticket.integration.bankda.service.BankOrderService;
import kr.co.winnticket.integration.bankda.dto.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bankda")
public class BankOrderController {

    private final BankOrderService bankOrderService;

    /**
     * 미입금 주문 조회
     */
    @GetMapping("/orders")
    public BankOrderResponse getUnpaidOrders() {
        return bankOrderService.getUnpaidOrders();
    }

    @PostMapping("/orders/detail")
    public BankOrderDetailResponse getOrderDetail(
            @RequestBody BankOrderDetailRequest req
    ) {
        return bankOrderService.getOrderDetail(req.getOrder_id());
    }

    @PostMapping("/orders/confirm")
    public BankConfirmResponse confirmOrders(
            @RequestBody BankConfirmRequest req
    ) {
        return bankOrderService.confirmOrders(req);
    }

}
