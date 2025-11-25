package kr.co.winnticket.order.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.order.admin.dto.OrderAdminListGetResDto;
import kr.co.winnticket.order.admin.dto.OrderAdminStatusGetResDto;
import kr.co.winnticket.order.admin.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "주문", description = "주문 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService service;

    // 주문 상태별 카운트 조회
    @GetMapping("/admin/status")
    @Operation(summary = "주문 상태별 카운트/총액 조회", description = "QNA 상태별 카운트및 총액을 조회합니다.")
    public OrderAdminStatusGetResDto getOrderStatusCount(
    ) throws Exception {
        return service.selectOrderAdminStatus();
    }

    // 주문 목록조회 (관리자)
    @GetMapping("/admin")
    @Operation(summary = "주문 목록 조회(관리자)", description = "주문 목록을 조회합니다.")
    public List<OrderAdminListGetResDto> getOrderAdminList (
            @Parameter(description = "검색어") @RequestParam(value = "srchWord", required = false) String asSrchWord,
            @Parameter(description = "시작일자") @RequestParam(value = "begDate",  required = false) LocalDate asBegDate,
            @Parameter(description = "종료일자") @RequestParam(value = "endDate",  required = false) LocalDate asEndDate,
            @Parameter(description = "상태 [ALL:전체, PENDING_PAYMENT:입금전, COMPLETED:주문처리완료, CANCEL_REQUESTED:취소신청, CANCELED:취소완료, REFUNDED:환불완료]") @RequestParam(value = "status",  required = false) String status
    ) throws Exception {
        return service.selectOrderAdminList(asSrchWord, asBegDate, asEndDate, status);
    }
}
