package kr.co.winnticket.order.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.order.shop.dto.OrderCreateReqDto;
import kr.co.winnticket.order.shop.dto.OrderCreateResDto;
import kr.co.winnticket.order.shop.service.OrderShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@Tag(name = "주문_쇼핑몰", description = "주문 관리")
@RestController
@RequiredArgsConstructor
public class OrderShopController {
    private final OrderShopService service;

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
