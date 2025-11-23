package kr.co.winnticket.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.community.qna.dto.QnaCntGetResDto;
import kr.co.winnticket.order.dto.OrderStatusCountGetResDto;
import kr.co.winnticket.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "주문", description = "주문 관리")
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    // QNA 상태별 카운트 조회
    @GetMapping("api/order/status/count")
    @Operation(summary = "주문 상태별 카운트/총액 조회", description = "QNA 상태별 카운트및 총액을 조회합니다.")
    public OrderStatusCountGetResDto getOrderStatusCount(
    ) throws Exception {
        return service.selectOrderStatusCount();
    }
}
