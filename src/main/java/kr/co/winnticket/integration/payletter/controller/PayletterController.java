package kr.co.winnticket.integration.payletter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.winnticket.integration.payletter.dto.*;
import kr.co.winnticket.integration.payletter.service.PayletterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public PayletterPaymentResDto requestPayment(
            @Parameter(description = "주문 ID") @PathVariable UUID orderId,
            @Parameter(description = "주문번호") @RequestParam String orderNumber,
            @Parameter(description = "결제 금액") @RequestParam Integer amount,
            @Parameter(description = "주문자 이름") @RequestParam String customerName,
            @Parameter(description = "주문자 이메일") @RequestParam(required = false) String customerEmail,
            @Parameter(description = "주문자 핸드폰") @RequestParam(required = false) String customerPhone,
            @Parameter(description = "결제수단 코드") @RequestParam String pgCode
    ) {

        return service.paymentRequest(orderId, orderNumber, amount, customerName, customerEmail, customerPhone,pgCode);
    }

    // Payletter 콜백 (성공시에만 옴)

//    @PostMapping("/callback")
//    @Operation(summary = "Payletter callback_url", description = "결제 성공 시 Payletter가 호출")
//    public PayletterCallbackResDto callback(@RequestBody Map<String, Object> payload) {
//        try {
//            service.handleCallback(payload);
//            return new PayletterCallbackResDto(0, "OK");
//        } catch (Exception e) {
//            log.error("[PAYLETTER] callback error", e);
//            return new PayletterCallbackResDto(1, e.getMessage());
//        }
//    }
// Payletter 콜백 (성공시에만 옴)
    @PostMapping("/callback")
    @Operation(summary = "Payletter callback_url", description = "결제 성공 시 Payletter가 호출")
    public PayletterCallbackResDto callback(@RequestBody PayletterCallbackReqDto req) {
        try {
            service.handleCallback(req.getPayload());
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

    @PostMapping("/cancel/{orderId}")
    @Operation(summary = "Payletter 결제취소", description = "Payletter 주문취소 API 호출 후 orders.payment_status=CANCELED 처리")
    public PayletterCancelResDto cancel(
            @Parameter(description = "주문_ID") @PathVariable UUID orderId, HttpServletRequest request) {

        String ipAddr = request.getRemoteAddr();
        log.info("[ADMIN][PAYLETTER] cancel request orderId={}", orderId);
        return service.cancel(orderId, ipAddr);
    }


    @GetMapping("/transaction/list")
    @Operation(summary = "Payletter 결제내역조회", description = "Payletter 거래내역 조회(transaction/list)")
    public PayletterTransactionListResDto transactionList(
            @Parameter(description = "조회 일자", example = "yyyyMMdd")
            @RequestParam(required = false) String date,

            @Parameter(description = "조회 기준", example = "transaction: 결제일기준 /settle:취소일기준")
            @RequestParam(required = false) String dateType,

            @Parameter(description = "결제수단 코드", example = "creditcard(테스트는kakaopay)")
            @RequestParam(required = false) String pgCode,

            @Parameter(description = "주문번호")
            @RequestParam(required = false) String orderNumber
    ) {
        return service.getTransactionList(date, dateType, pgCode, orderNumber);
    }

    @GetMapping("/status/{orderNumber}")
    @Operation(summary = "Payletter 거래상태조회", description = "Payletter 거래상태 조회(payments/status)")
    public PayletterPaymentStatusResDto paymentStatus(@PathVariable String orderNumber) {
        return service.getPaymentStatus(orderNumber);
    }



}
