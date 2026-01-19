package kr.co.winnticket.integration.payletter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.integration.payletter.dto.PayletterCallbackResDto;
import kr.co.winnticket.integration.payletter.dto.PayletterPaymentResDto;
import kr.co.winnticket.integration.payletter.service.PayletterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;


import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payletter")
@Tag(name = "Paylatter" , description = "Paylatter 연동 API")
public class PayletterController {

    private final PayletterService service;

    // (테스트용) 결제요청 API
    @PostMapping("/request/{orderId}")
    @Operation(summary = "Payletter 결제요청(테스트)", description = "orderId로 결제요청 후 결제 URL 리턴")
    public PayletterPaymentResDto requestPayment(@PathVariable UUID orderId,
                                                 @RequestParam String orderNumber,
                                                 @RequestParam Integer amount,
                                                 @RequestParam String customerName,
                                                 @RequestParam(required = false) String customerEmail,
                                                 @RequestParam(required = false) String customerPhone) {

        return service.paymentRequest(orderId, orderNumber, amount, customerName, customerEmail, customerPhone);
    }

    // Payletter 콜백 (성공시에만 옴)
    @PostMapping("/callback")
    @Operation(summary = "Payletter callback_url", description = "결제 성공 시 Payletter가 호출")
    public PayletterCallbackResDto callback(@RequestBody Map<String, Object> payload) {
        try {
            service.handleCallback(payload);
            return new PayletterCallbackResDto(0, "OK");
        } catch (Exception e) {
            log.error("[PAYLETTER] callback error", e);
            return new PayletterCallbackResDto(1, e.getMessage());
        }
    }

    @GetMapping("/return")
    public String payReturn() {
        return "RETURN_OK";
    }

    @GetMapping("/cancel")
    public String payCancel() {
        return "CANCEL_OK";
    }

}
