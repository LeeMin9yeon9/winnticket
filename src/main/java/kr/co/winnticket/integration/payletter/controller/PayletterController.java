package kr.co.winnticket.integration.payletter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.integration.payletter.dto.PayletterPaymentStatusResDto;
import kr.co.winnticket.integration.payletter.dto.PayletterTransactionListResDto;
import kr.co.winnticket.integration.payletter.service.PayletterService;
import kr.co.winnticket.order.admin.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payletter")
@Tag(name = "Paylatter" , description = "Paylatter 연동 API")
public class PayletterController {

    private final PayletterService service;
    private final OrderService orderService;

    @PostMapping("/callback")
    @Operation(summary = "Payletter 콜백", description = "Payletter 결제 성공 알림")
    public ApiResponse<String> callback(@RequestBody Map<String, Object> payload){

        service.handleCallback(payload);

        String orderNumber = String.valueOf(payload.get("custom_parameter"));

        orderService.completePaymentByOrderNumber(orderNumber);


        log.info("[PAYLETTER] callback payload={}", payload);

        return ApiResponse.success("OK");
    }


    @GetMapping("/return")
    @Operation(summary = "Payletter 결제 완료 후 redirect", description = "Payletter 결제 완료 후 페이지 이동")
    public void payReturn(
            @RequestParam(required = false) String custom_parameter, HttpServletResponse response
    ) throws IOException {

        log.info("[PAYLETTER] return custom_parameter={}", custom_parameter);

        response.sendRedirect(
                "https://www.winnticket.store/payment-success?orderNumber=" + custom_parameter
        );
    }

    @GetMapping("/cancel")
    @Operation(summary = "Payletter 결제 취소 redirect", description = "Payletter 결제 취소 후 주문페이지 이동")
    public String payCancel(
            @RequestParam(required = false) String custom_parameter) {

        log.info("Payletter cancel orderId={}", custom_parameter);

        return "redirect:https://www.winnticket.store/order";
    }

//    @PostMapping("/cancel/{orderId}")
//    @Operation(summary = "Payletter 결제취소", description = "Payletter 주문취소 API 호출 후 orders.payment_status=CANCELED 처리")
//    public PayletterCancelResDto cancel(
//            @Parameter(description = "주문_ID") @PathVariable UUID orderId, HttpServletRequest request) {
//
//        String ipAddr = request.getRemoteAddr();
//        log.info("[ADMIN][PAYLETTER] cancel request orderId={}", orderId);
//        return service.cancel(orderId, ipAddr);
//    }


    @GetMapping("/transaction/list")
    @Operation(summary = "Payletter 결제내역조회", description = "Payletter 거래내역 조회(transaction/list)")
    public PayletterTransactionListResDto transactionList(
            @Parameter(description = "조회 일자", example = "yyyyMMdd")
            @RequestParam(required = false) String date,

            @Parameter(description = "조회 기준", example = "transaction: 결제일기준 /settle:취소일기준")
            @RequestParam(required = false) String dateType,

            @Parameter(description = "결제수단 코드", example = "creditcard(테스트는kakaopay)")
            @RequestParam(required = false) String paymentMethod,

            @Parameter(description = "주문번호")
            @RequestParam(required = false) String orderNumber
    ) {
        return service.getTransactionList(date, dateType, paymentMethod, orderNumber);
    }

    @GetMapping("/status/{orderNumber}")
    @Operation(summary = "Payletter 거래상태조회", description = "Payletter 거래상태 조회(payments/status)")
    public PayletterPaymentStatusResDto paymentStatus(@PathVariable String orderNumber) {
        return service.getPaymentStatus(orderNumber);
    }

//    @GetMapping("/test/hash")
//    @Operation(summary = "Payletter 테스트 hash 생성", description = "Payletter hash 테스트용")
//    public String testHash(@RequestParam String userId, @RequestParam String tid, @RequestParam Integer amount) {
//        String apiKey = properties.getPaymentApiKey();
//
//        return PayletterHashUtil.makePayhash(userId, tid, amount, apiKey);
//
//    }



}
