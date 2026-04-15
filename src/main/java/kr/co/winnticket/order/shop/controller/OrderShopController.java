package kr.co.winnticket.order.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.order.admin.service.OrderService;
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
    @Operation(summary = "주문 생성", description = "장바구니 정보를 기반으로 주문을 생성합니다.")
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

    // 소비자 주문 취소
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "소비자 주문 취소", description = "소비자가 주문을 취소합니다. (결제 환불 포함)")
    public ResponseEntity<ApiResponse<String>> cancelOrder(
            @PathVariable UUID orderId
    ) throws Exception {
        log.info("[소비자 취소] orderId={}", orderId);
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("주문 취소 완료", orderId.toString()));
    }

    // QR 쿠폰 조회
    @Operation(summary = "QR 쿠폰 조회")
    @GetMapping("/coupon/{orderNumber}")
    public ApiResponse getQrCoupon(
            @PathVariable String orderNumber){
        return ApiResponse.success(service.getQrCoupon(orderNumber));
    }

}