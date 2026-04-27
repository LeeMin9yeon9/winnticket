package kr.co.winnticket.integration.payletter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointCancelReqDto;
import kr.co.winnticket.integration.benepia.kcp.service.KcpService;
import kr.co.winnticket.integration.payletter.config.PayletterProperties;
import kr.co.winnticket.integration.payletter.dto.PayletterPaymentStatusResDto;
import kr.co.winnticket.integration.payletter.dto.PayletterTransactionListResDto;
import kr.co.winnticket.integration.payletter.service.PayletterService;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.admin.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payletter")
@Tag(name = "Paylatter" , description = "Paylatter 연동 API")
public class PayletterController {

    private final PayletterService service;
    private final PayletterProperties properties;
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final KcpService kcpService;

    @PostMapping("/callback")
    @Operation(summary = "Payletter 콜백", description = "Payletter 결제 성공 알림")
    public Map<String, Object> callback(@RequestBody Map<String, Object> payload, HttpSession session) {

        log.info("[PAYLETTER] callback received payload={}", payload);

        try {
            service.handleCallback(payload);

            Object param = payload.get("custom_parameter");
            if (param != null) {
                UUID orderId = UUID.fromString(String.valueOf(param));
                orderService.completePayment(orderId);
                log.info("[PAYLETTER] payment processed orderId={}", orderId);
            } else {
                log.warn("[PAYLETTER] custom_parameter missing payload={}", payload);
            }

            log.info("[PAYLETTER] callback response code=0");
            return Map.of(
                    "code", 0,
                    "message", "success"
            );

        } catch (Exception e) {
            log.error("[PAYLETTER CALLBACK ERROR] payload={}", payload, e);
            return Map.of(
                    "code", 1,
                    "message", "fail"
            );
        }
    }

    @RequestMapping(value = "/return", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Payletter 결제 완료 후 redirect", description = "Payletter 결제 완료 후 페이지 이동")
    public void payReturn(
            @RequestParam(required = false) String custom_parameter, HttpServletResponse response
    ) throws IOException {

        log.info("[PAYLETTER] return custom_parameter={}", custom_parameter);

        String orderNumber = custom_parameter;
        try {
            UUID orderId = UUID.fromString(custom_parameter);
            String found = orderMapper.findOrderNumberById(orderId);
            if (found != null) {
                orderNumber = found;
            }
        } catch (Exception e) {
            log.warn("[PAYLETTER] custom_parameter is not UUID, using as-is: {}", custom_parameter);
        }

        response.sendRedirect(
                properties.getFrontUrl() + "/payment-success?orderNumber=" + orderNumber
        );
    }

    @RequestMapping(value = "/cancel", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Payletter 결제 취소 redirect", description = "결제창 종료/취소 시 이동 처리")
    public void payCancel(
            @RequestParam(required = false) String custom_parameter,@RequestParam(required = false) String order_no, HttpServletResponse response
    ) throws IOException {

        log.info("[PAYLETTER] cancel hit custom_parameter={}, order_no={}", custom_parameter, order_no);

        try {

            UUID orderId = null;

            // custom_parameter
            if (custom_parameter != null && !custom_parameter.isBlank()) {
                try {
                    orderId = UUID.fromString(custom_parameter);
                } catch (Exception ignore) {}
            }

            if (orderId == null && order_no != null) {
                orderId = orderMapper.findOrderIdByOrderNumber(order_no);
            }

            if (orderId != null) {

                Map<String, Object> paymentInfo = orderMapper.selectOrderPaymentInfo(orderId);

                if (paymentInfo != null) {

                    Integer pointAmount = paymentInfo.get("point_amount") != null
                            ? ((Number) paymentInfo.get("point_amount")).intValue()
                            : 0;

                    String pointTid = (String) paymentInfo.get("point_tid");
                    String orderNumber = (String) paymentInfo.get("order_number");

                    if (pointAmount > 0 && pointTid != null) {

                        try {
                            KcpPointCancelReqDto dto = new KcpPointCancelReqDto();
                            dto.setTno(pointTid);
                            dto.setCancelReason("사용자 결제 취소 / orderNo=" + orderNumber);

                            kcpService.cancelPoint(dto);

                            log.info("[POINT ROLLBACK] success orderId={}", orderId);

                        } catch (Exception e) {
                            log.error("[POINT ROLLBACK FAIL]", e);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("[PAYLETTER CANCEL ERROR]", e);
        } finally {
            response.sendRedirect(properties.getFrontUrl() + "/order");
        }
    }



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


}
