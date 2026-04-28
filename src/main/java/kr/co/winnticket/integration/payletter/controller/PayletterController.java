package kr.co.winnticket.integration.payletter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.common.enums.ProductType;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointCancelReqDto;
import kr.co.winnticket.integration.benepia.kcp.service.BenepiaCredentialStore;
import kr.co.winnticket.integration.benepia.kcp.service.KcpService;
import kr.co.winnticket.integration.payletter.config.PayletterProperties;
import kr.co.winnticket.integration.payletter.dto.PayletterPaymentStatusResDto;
import kr.co.winnticket.integration.payletter.dto.PayletterTransactionListResDto;
import kr.co.winnticket.integration.payletter.service.PayletterService;
import kr.co.winnticket.order.admin.dto.OrderItemOptionDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.admin.service.OrderService;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import kr.co.winnticket.ticketCoupon.service.TicketCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
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
    private final OrderShopMapper orderShopMapper;
    private final KcpService kcpService;
    private final BenepiaCredentialStore benepiaCredentialStore;
    private final TicketCouponService ticketCouponService;

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
        String channelCode = null;
        try {
            UUID orderId = UUID.fromString(custom_parameter);
            String found = orderMapper.findOrderNumberById(orderId);
            if (found != null) {
                orderNumber = found;
            }
            Map<String, Object> paymentInfo = orderMapper.selectOrderPaymentInfo(orderId);
            if (paymentInfo != null) {
                channelCode = (String) paymentInfo.get("channel_code");
            }
        } catch (Exception e) {
            log.warn("[PAYLETTER] custom_parameter is not UUID, using as-is: {}", custom_parameter);
        }

        String redirectUrl = properties.getFrontUrl() + "/payment-success?orderNumber=" + orderNumber;
        if (channelCode != null && !channelCode.isBlank()) {
            redirectUrl += "&channel=" + channelCode;
        }
        response.sendRedirect(redirectUrl);
    }

    @RequestMapping(value = "/cancel", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Payletter 결제 취소 redirect", description = "결제창 종료/취소 시 이동 처리")
    public void payCancel(
            @RequestParam(required = false) String custom_parameter,@RequestParam(required = false) String order_no, HttpServletResponse response
    ) throws IOException {

        log.info("[PAYLETTER] cancel hit custom_parameter={}, order_no={}", custom_parameter, order_no);

        String channelCode = null;

        try {

            UUID orderId = null;

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

                    channelCode = (String) paymentInfo.get("channel_code");
                    String orderNumber = (String) paymentInfo.get("order_number");

                    // 1. 포인트가 실제로 차감된 경우(point_tid 있음)만 환불
                    String pointTid = (String) paymentInfo.get("point_tid");
                    if (pointTid != null && !pointTid.isBlank()) {
                        try {
                            KcpPointCancelReqDto dto = new KcpPointCancelReqDto();
                            dto.setTno(pointTid);
                            dto.setCancelReason("사용자 결제 취소 / orderNo=" + orderNumber);
                            kcpService.cancelPoint(dto);
                            log.info("[CANCEL] 포인트 환불 완료 orderId={}", orderId);
                        } catch (Exception e) {
                            log.error("[CANCEL] 포인트 환불 실패 orderId={}", orderId, e);
                        }
                    }

                    // 2. Redis 인증 정보 즉시 삭제 (혼합결제 대기 중이었던 경우)
                    benepiaCredentialStore.delete(orderId);

                    // 3. 주문 상태 즉시 FAILED/CANCELED (REQUESTED 상태인 경우만)
                    int updated = orderShopMapper.updateCancelIfRequested(orderId);

                    if (updated > 0) {
                        log.info("[CANCEL] 주문 즉시 취소 완료 orderId={}", orderId);

                        // 4. 재고 복구
                        try {
                            List<OrderItemOptionDto> options = orderMapper.selectOrderItemOptions(orderId);
                            for (OrderItemOptionDto opt : options) {
                                if (!ProductType.STAY.equals(opt.getProductType())) {
                                    orderMapper.increaseStock(opt.getOptionValueId(), opt.getQuantity());
                                }
                            }
                            log.info("[CANCEL] 재고 복구 완료 orderId={}", orderId);
                        } catch (Exception e) {
                            log.error("[CANCEL] 재고 복구 실패 orderId={}", orderId, e);
                        }

                        // 5. 예약쿠폰 복구
                        try {
                            ticketCouponService.restoreReservedCoupons(orderId);
                            log.info("[CANCEL] 쿠폰 복구 완료 orderId={}", orderId);
                        } catch (Exception e) {
                            log.error("[CANCEL] 쿠폰 복구 실패 orderId={}", orderId, e);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("[PAYLETTER CANCEL ERROR]", e);
        } finally {
            String redirectUrl = properties.getFrontUrl() + "/order";
            if (channelCode != null && !channelCode.isBlank()) {
                redirectUrl += "?channel=" + channelCode;
            }
            response.sendRedirect(redirectUrl);
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
