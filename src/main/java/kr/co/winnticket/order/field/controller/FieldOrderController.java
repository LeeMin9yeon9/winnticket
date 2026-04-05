package kr.co.winnticket.order.field.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.auth.jwt.JwtTokenProvider;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.order.field.dto.FieldOrderListGetResDto;
import kr.co.winnticket.order.field.dto.FieldOrderStatusGetResDto;
import kr.co.winnticket.order.field.service.FieldOrderService;
import kr.co.winnticket.order.admin.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "현장관리자 주문관리", description = "현장관리자 전용 주문 관리")
@RequestMapping("/api/admin/field-order")
@RestController
@RequiredArgsConstructor
public class FieldOrderController {

    private final FieldOrderService fieldOrderService;
    private final OrderService orderService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * JWT에서 partnerId 추출
     */
    private UUID extractPartnerId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        var claims = jwtTokenProvider.getClaims(token);
        String partnerId = (String) claims.get("partnerId");
        if (partnerId == null) {
            throw new IllegalStateException("현장관리자에게 partnerId가 없습니다.");
        }
        return UUID.fromString(partnerId);
    }

    // 현장관리자 주문 통계
    @GetMapping("/status")
    @Operation(summary = "현장관리자 주문 통계", description = "현장관리자의 파트너 소속 주문 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<FieldOrderStatusGetResDto>> getFieldOrderStatus(
            @Parameter(description = "시작일자") @RequestParam(value = "begDate", required = false) LocalDate begDate,
            @Parameter(description = "종료일자") @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @Parameter(description = "상태") @RequestParam(value = "status", required = false) String status,
            HttpServletRequest request
    ) {
        UUID partnerId = extractPartnerId(request);
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", fieldOrderService.selectFieldOrderStatus(partnerId, begDate, endDate, status))
        );
    }

    // 현장관리자 주문(티켓) 목록 조회
    @GetMapping
    @Operation(summary = "현장관리자 주문(티켓) 목록 조회", description = "현장관리자의 파트너 소속 주문 티켓 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FieldOrderListGetResDto>>> getFieldOrderList(
            @Parameter(description = "검색어") @RequestParam(value = "srchWord", required = false) String srchWord,
            @Parameter(description = "시작일자") @RequestParam(value = "begDate", required = false) LocalDate begDate,
            @Parameter(description = "종료일자") @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @Parameter(description = "상태 [ALL:전체, USED:사용완료, UNUSED:미사용, CANCELED:취소, EXPIRED:기간만료]")
            @RequestParam(value = "status", required = false) String status,
            HttpServletRequest request
    ) {
        UUID partnerId = extractPartnerId(request);
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", fieldOrderService.selectFieldOrderList(partnerId, srchWord, begDate, endDate, status))
        );
    }

    // 현장관리자 티켓 조회
    @GetMapping("/tickets/{orderId}/{ticketId}")
    @Operation(summary = "현장관리자 티켓 조회", description = "현장관리자가 주문의 티켓을 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getFieldOrderTickets(
            @Parameter(description = "주문_ID") @PathVariable UUID orderId,
            @Parameter(description = "티켓_ID") @PathVariable UUID ticketId
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", orderService.selectOrderAdminTicketList(orderId, ticketId))
        );
    }

    // 티켓 사용 처리 (기존 OrderService 재활용)
    @PostMapping("/tickets/{orderId}/{ticketId}/use")
    @Operation(summary = "티켓 사용완료 처리", description = "현장관리자가 티켓을 사용완료 처리합니다.")
    public ResponseEntity<ApiResponse<String>> useTicket(
            @PathVariable UUID orderId,
            @PathVariable UUID ticketId
    ) throws Exception {
        orderService.useTicket(orderId, ticketId);
        return ResponseEntity.ok(
                ApiResponse.success("티켓사용 완료", ticketId.toString())
        );
    }
}
