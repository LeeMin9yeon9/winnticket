package kr.co.winnticket.order.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.order.admin.service.OrderService;
import kr.co.winnticket.order.shop.dto.OrderCancelRequestReqDto;
import kr.co.winnticket.order.shop.dto.OrderCreateReqDto;
import kr.co.winnticket.order.shop.dto.OrderCreateResDto;
import kr.co.winnticket.order.shop.dto.OrderShopGetResDto;
import kr.co.winnticket.order.shop.service.OrderShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Tag(name = "주문_쇼핑몰", description = "주문 관리")
@RequestMapping("/api/orders/shop")
@RestController
@RequiredArgsConstructor
public class OrderShopController {
    private final OrderShopService service;
    private final OrderService orderService;

    // 주문 조회
    @GetMapping("/{channelId}/{orderNumber}")
    @Operation(summary = "주문 조회", description = "전달받은 주문번호의 주문을 조회합니다.")
    public ResponseEntity<ApiResponse<OrderShopGetResDto>> getOrderShop (
            @Parameter(description = "채널id") @PathVariable("channelId") UUID channelId,
            @Parameter(description = "주문번호") @PathVariable("orderNumber") String orderNumber
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectOrderShop(channelId, orderNumber))
        );
    }

    // 주문 생성
    @PostMapping
    @Operation(summary = "주문 생성", description = "장바구니 정보를 기반으로 주문을 생성합니다. paymentMethod에 POINT(포인트 단독/포인트+이용권)·GIFT(이용권 단독결제, voucherAmount가 결제금액과 정확히 일치해야 함) 사용 가능. 이용권 결제 시 voucherNumber/voucherAmount 필수. 포인트+이용권 조합은 paymentMethod=POINT일 때만 허용되며, 카드/무통장과 포인트+이용권을 동시에 쓰는 3중 혼합은 금지됩니다.")
    public ResponseEntity<ApiResponse<OrderCreateResDto>> createOrder(
            @RequestBody @Valid OrderCreateReqDto model,
            HttpSession session
    ) {
        log.info("주문 생성 요청 DTO = {}", model);
        OrderCreateResDto response = service.createOrder(model,session);

        return ResponseEntity.ok(
                ApiResponse.success("주문 성공", response)
        );
    }

    // 주문 취소 (관리자/내부용) - 실제 취소를 즉시 처리. 고객 자가취소는 더 이상 이 API를 쓰지 않고 /cancel-request로 "요청"만 함
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소(쇼핑몰)", description = "주문을 즉시 취소 처리합니다.")
    public ResponseEntity<ApiResponse<String>> cancelOrder(
            @Parameter(description = "주문ID") @PathVariable("orderId") UUID orderId
    ) throws Exception {
        log.info("[소비자 취소] orderId={}", orderId);
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("주문 취소 완료", orderId.toString()));
    }

    // 주문 취소 요청 (쇼핑몰) - 고객은 취소를 "요청"만 하고, 관리자가 확인 후 실제 취소를 진행함.
    // 무통장입금이 포함된 결제는 환불계좌 정보가 필수(자동 환불 불가), 그 외 결제수단은 계좌 정보 없이 요청만 접수.
    @PostMapping("/{orderId}/cancel-request")
    @Operation(summary = "주문 취소 요청(쇼핑몰)", description = "고객이 주문 취소를 요청합니다 (상태만 취소신청으로 전환, 실제 취소는 관리자가 확인 후 처리). 무통장입금 포함 결제는 bankName/accountNumber/accountHolder가 필수입니다.")
    public ResponseEntity<ApiResponse<String>> cancelOrderRequest(
            @Parameter(description = "주문ID") @PathVariable("orderId") UUID orderId,
            @RequestBody(required = false) OrderCancelRequestReqDto reqDto
    ) {
        log.info("[소비자 취소요청] orderId={}", orderId);
        String bankName = reqDto != null ? reqDto.getBankName() : null;
        String accountNumber = reqDto != null ? reqDto.getAccountNumber() : null;
        String accountHolder = reqDto != null ? reqDto.getAccountHolder() : null;
        service.requestCancel(orderId, bankName, accountNumber, accountHolder);
        return ResponseEntity.ok(ApiResponse.success("취소 요청이 접수되었습니다. 담당자 확인 후 처리됩니다.", orderId.toString()));
    }

    // QR 쿠폰 조회
    @Operation(summary = "QR 쿠폰 조회")
    @GetMapping("/coupon/{orderNumber}")
    public ApiResponse getQrCoupon(
            @PathVariable String orderNumber){
        return ApiResponse.success(service.getQrCoupon(orderNumber));
    }

}