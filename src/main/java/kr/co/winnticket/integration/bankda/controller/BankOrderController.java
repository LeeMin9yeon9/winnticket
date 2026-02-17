package kr.co.winnticket.integration.bankda.controller;

import kr.co.winnticket.integration.bankda.service.BankOrderService;
import kr.co.winnticket.integration.bankda.dto.*;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return bankOrderService.selectBankdaOrders();
    }

    /**
     * 주문 상세 조회
     */
    @PostMapping("/order/detail")
    public BankOrderDetailResponse getOrderDetail(
            @RequestBody BankOrderDetailRequest req
    ) {
        System.out.println("REQ = " + req);
        BankOrderDetailResponse res = bankOrderService.getOrderDetail(req.getOrderId());
        System.out.println("🔥 response = " + res);
        return res;
    }

    /**
     * 주문 입금완료 처리
     */
    @PostMapping("/orders/confirm")
    public ResponseEntity<BankConfirmResponse> confirm(
            @RequestBody BankConfirmRequest request
    ) {
        return bankOrderService.confirm(request);
    }
}
