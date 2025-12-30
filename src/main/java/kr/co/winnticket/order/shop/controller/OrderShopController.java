package kr.co.winnticket.order.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import kr.co.winnticket.order.admin.dto.OrderAdminDetailGetResDto;
import kr.co.winnticket.order.shop.dto.OrderShopGetResDto;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.order.shop.dto.OrderCreateReqDto;
import kr.co.winnticket.order.shop.dto.OrderCreateResDto;
import kr.co.winnticket.order.shop.service.OrderShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Slf4j
@Tag(name = "주문_쇼핑몰", description = "주문 관리")
@RestController
@RequiredArgsConstructor
public class OrderShopController {
    private final OrderShopService service;

    // 주문 조회
    @GetMapping("api/orders/shop/{orderNumber}")
    @Operation(summary = "주문 조회", description = "전달받은 주문번호의 주문을 조회합니다.")
    public ResponseEntity<ApiResponse<OrderShopGetResDto>> getOrderShop (
            @Parameter(description = "주문번호") @PathVariable("orderNumber") String orderNumber
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectOrderShop(orderNumber))
        );
    }

    // 주문 생성
    @PostMapping("api/orders/shop")
    @Operation(summary = "주문 생성", description = "장바구니 정보를 기반으로 주문을 생성합니다.")
    public ResponseEntity<ApiResponse<OrderCreateResDto>> createOrder(
            @RequestBody @Valid OrderCreateReqDto model
    ) {
        log.info("주문 생성 요청 DTO = {}", model);
        OrderCreateResDto response = service.createOrder(model);

        return ResponseEntity.ok(
                ApiResponse.success("주문 성공", response)
        );
    }
}
