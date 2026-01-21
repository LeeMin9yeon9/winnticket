package kr.co.winnticket.order.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.integration.payletter.dto.PayletterCancelResDto;
import kr.co.winnticket.integration.payletter.service.PayletterService;
import kr.co.winnticket.order.admin.dto.OrderAdminDetailGetResDto;
import kr.co.winnticket.order.admin.dto.OrderAdminListGetResDto;
import kr.co.winnticket.order.admin.dto.OrderAdminStatusGetResDto;
import kr.co.winnticket.order.admin.dto.OrderAdminTicketCheckGetResDto;
import kr.co.winnticket.order.admin.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "주문", description = "주문 관리")
@RequestMapping("/api/admin/order")
@RestController
@RequiredArgsConstructor

public class OrderController {
    private final OrderService service;
    private final PayletterService payletterService;

    // 주문 상태별 카운트 조회
    @GetMapping("/status")
    @Operation(summary = "주문 상태별 카운트/총액 조회", description = "QNA 상태별 카운트및 총액을 조회합니다.")
    public ResponseEntity<ApiResponse<OrderAdminStatusGetResDto>> getOrderStatusCount(
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectOrderAdminStatus())
        );
    }

    // 주문 목록조회 (관리자)
    @GetMapping
    @Operation(summary = "주문 목록 조회(관리자)", description = "주문 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<OrderAdminListGetResDto>>> getOrderAdminList (
            @Parameter(description = "검색어") @RequestParam(value = "srchWord", required = false) String asSrchWord,
            @Parameter(description = "시작일자") @RequestParam(value = "begDate",  required = false) LocalDate asBegDate,
            @Parameter(description = "종료일자") @RequestParam(value = "endDate",  required = false) LocalDate asEndDate,
            @Parameter(description = "상태 [ALL:전체, PENDING_PAYMENT:입금전, COMPLETED:주문처리완료, CANCEL_REQUESTED:취소신청, CANCELED:취소완료, REFUNDED:환불완료]") @RequestParam(value = "status",  required = false) String status
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectOrderAdminList(asSrchWord, asBegDate, asEndDate, status))
        );
    }

    // 주문 상세조회 (관리자)
    @GetMapping("/{id}")
    @Operation(summary = "주문 상세 조회(관리자)", description = "전달받은 id의 주문을 조회합니다.")
    public ResponseEntity<ApiResponse<OrderAdminDetailGetResDto>> getOrderAdminDetail (
            @Parameter(description = "주문_ID") @PathVariable("id") UUID auId
   ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectOrderAdminDetail(auId))
        );
    }

    // 티켓조회 (현장관리자)
    @GetMapping("/tickets/{id}")
    @Operation(summary = "티켓 조회(현장관리자)", description = "전달받은 주문id의 모든 티켓을 조회합니다.")
    public ResponseEntity<ApiResponse<OrderAdminTicketCheckGetResDto>> getOrderAdminTicketList (
            @Parameter(description = "주문_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectOrderAdminTicketList(auId))
        );
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "주문 결제 완료 처리", description = "전달받은 id의 주문을 결재완료 처리합니다.")
    public ResponseEntity<ApiResponse<String>> completePayment(
            @Parameter(description = "주문_ID") @PathVariable("id") UUID auId
    ) throws Exception {
        service.completePayment(auId);

        String id = auId.toString();

        return ResponseEntity.ok(
                ApiResponse.success("결제 완료", id)
        );
    }

    @PostMapping("/tickets/{orderId}/{ticketId}/use")
    @Operation(summary = "티켓 사용완료 처리", description = "전달받은 id의 티켓을 사용완료 처리합니다.")
    public ResponseEntity<ApiResponse<String>> useTicket(
            @PathVariable UUID orderId,
            @PathVariable UUID ticketId
    ) throws Exception {
        service.useTicket(orderId, ticketId);

        String id = ticketId.toString();

        return ResponseEntity.ok(
                ApiResponse.success("티켓사용 완료", id)
        );
    }

    // payletter 취소(환불) API
    @PostMapping("/payletter/cancel/{orderId}")
    @Operation(summary = "Payletter 결제취소", description = "Payletter 주문취소 API 호출 후 orders.payment_status=CANCELED 처리")
    public PayletterCancelResDto cancel(@PathVariable UUID orderId, HttpServletRequest request) {

        String ipAddr = request.getRemoteAddr();

        return payletterService.cancel(orderId, ipAddr);
    }
}
