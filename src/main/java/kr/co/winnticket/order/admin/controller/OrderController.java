package kr.co.winnticket.order.admin.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.common.enums.PaymentMethod;
import kr.co.winnticket.common.enums.PaymentStatus;
import kr.co.winnticket.integration.payletter.service.PayletterService;
import kr.co.winnticket.order.admin.dto.OrderAdminDetailGetResDto;
import kr.co.winnticket.order.admin.dto.OrderAdminListGetResDto;
import kr.co.winnticket.order.admin.dto.OrderAdminStatusGetResDto;
import kr.co.winnticket.order.admin.dto.OrderAdminTicketCheckGetResDto;
import kr.co.winnticket.order.admin.dto.OrderExportResDto;
import kr.co.winnticket.order.admin.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            @Parameter(description = "파트너Id") @RequestParam(value = "partnerId",  required = false) UUID partnerId,
            @Parameter(description = "상태 [ALL:전체, PENDING_PAYMENT:입금전, COMPLETED:주문처리완료, CANCEL_REQUESTED:취소신청, CANCELED:취소완료, REFUNDED:환불완료]") @RequestParam(value = "status",  required = false) String status,
            @Parameter(description = "채널Id") @RequestParam(value = "channelId",  required = false) UUID channelId
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectOrderAdminList(asSrchWord, asBegDate, asEndDate, partnerId, status, channelId))
        );
    }

    // 주문 목록 Excel 내보내기
    @GetMapping("/export")
    @Operation(summary = "주문 목록 Excel 내보내기", description = "현재 필터 조건으로 주문 목록을 Excel 파일로 다운로드합니다.")
    public ResponseEntity<byte[]> exportOrdersExcel(
            @Parameter(description = "검색어") @RequestParam(value = "srchWord", required = false) String asSrchWord,
            @Parameter(description = "시작일자") @RequestParam(value = "begDate", required = false) LocalDate asBegDate,
            @Parameter(description = "종료일자") @RequestParam(value = "endDate", required = false) LocalDate asEndDate,
            @Parameter(description = "상태") @RequestParam(value = "status", required = false) String status,
            @Parameter(description = "채널Id") @RequestParam(value = "channelId", required = false) UUID channelId
    ) throws Exception {
        List<OrderExportResDto> rows = service.selectOrderExportList(asSrchWord, asBegDate, asEndDate, status, channelId);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("주문목록");

        // 헤더 스타일
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        String[] headers = {
                "채널명", "주문일", "주문번호", "회사명", "부서명",
                "주문자 이름", "주문자 전화번호", "주문자 이메일",
                "수령자 이름", "수령자 전화번호",
                "상품번호", "주문상품", "예약일자", "상품종류", "티켓종류",
                "수량", "단가", "공급가", "총 주문금액",
                "결제상태", "결제금액", "결제수단",
                "베네피아 포인트 결제금액", "베네피아 아이디",
                "무통장 결제금액", "신용카드 결제금액", "이용권"
        };

        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 데이터 행
        int rowNum = 1;
        for (OrderExportResDto r : rows) {
            HSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(r.getChannelName() != null ? r.getChannelName() : "");
            row.createCell(1).setCellValue(r.getOrderedAt() != null ? r.getOrderedAt() : "");
            row.createCell(2).setCellValue(r.getOrderNumber() != null ? r.getOrderNumber() : "");
            row.createCell(3).setCellValue("");
            row.createCell(4).setCellValue("");
            row.createCell(5).setCellValue(r.getCustomerName() != null ? r.getCustomerName() : "");
            row.createCell(6).setCellValue(r.getCustomerPhone() != null ? r.getCustomerPhone() : "");
            row.createCell(7).setCellValue(r.getCustomerEmail() != null ? r.getCustomerEmail() : "");
            row.createCell(8).setCellValue(r.getRecipientName() != null ? r.getRecipientName() : "");
            row.createCell(9).setCellValue(r.getRecipientPhone() != null ? r.getRecipientPhone() : "");
            row.createCell(10).setCellValue(r.getProductCode() != null ? r.getProductCode() : "");
            row.createCell(11).setCellValue(r.getProductDisplayName() != null ? r.getProductDisplayName() : "");
            row.createCell(12).setCellValue("");
            row.createCell(13).setCellValue("");
            row.createCell(14).setCellValue(r.getTicketType() != null ? r.getTicketType() : "");
            row.createCell(15).setCellValue(r.getQuantity() != null ? r.getQuantity() : 0);
            row.createCell(16).setCellValue(r.getUnitPrice() != null ? r.getUnitPrice() : 0);
            row.createCell(17).setCellValue(r.getSupplyPrice() != null ? r.getSupplyPrice() : 0);
            row.createCell(18).setCellValue(r.getTotalOrderAmount() != null ? r.getTotalOrderAmount() : 0);
            // 결제상태 한글 변환
            String psDisplay = "";
            if (r.getPaymentStatus() != null) {
                try { psDisplay = PaymentStatus.valueOf(r.getPaymentStatus()).getDisplayName(); }
                catch (Exception e) { psDisplay = r.getPaymentStatus(); }
            }
            row.createCell(19).setCellValue(psDisplay);
            row.createCell(20).setCellValue(r.getFinalPrice() != null ? r.getFinalPrice() : 0);
            // 결제수단 한글 변환
            String pmDisplay = "";
            if (r.getPaymentMethod() != null) {
                try { pmDisplay = PaymentMethod.valueOf(r.getPaymentMethod()).getDisplayName(); }
                catch (Exception e) { pmDisplay = r.getPaymentMethod(); }
            }
            row.createCell(21).setCellValue(pmDisplay);
            row.createCell(22).setCellValue(r.getPointAmount() != null ? r.getPointAmount() : 0);
            row.createCell(23).setCellValue(r.getBenepiaId() != null ? r.getBenepiaId() : "");
            row.createCell(24).setCellValue(r.getBankTransferAmount() != null ? r.getBankTransferAmount() : 0);
            row.createCell(25).setCellValue(r.getCardAmount() != null ? r.getCardAmount() : 0);
            row.createCell(26).setCellValue("");
        }

        // 열 너비 자동 조정
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        String filename = "주문목록_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd")) + ".xls";
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(baos.toByteArray());
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
    @Operation(summary = "티켓 조회(현장관리자)", description = "전달받은 주문, 티켓id의 티켓을 조회합니다.")
    public ResponseEntity<ApiResponse<OrderAdminTicketCheckGetResDto>> getOrderAdminTicketList (
            @Parameter(description = "주문_ID") @PathVariable("id") UUID auId,
            @Parameter(description = "티켓_ID") @PathVariable UUID ticketId
    ) throws Exception {
        return ResponseEntity.ok(
                ApiResponse.success("조회 성공", service.selectOrderAdminTicketList(auId, ticketId))
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

    @PostMapping("{id}/cancel")
    @Operation(summary = "주문 취소(관리자)", description = "관리자가 주문을 취소합니다.")
    public ResponseEntity<ApiResponse<String>> cancelOrder(
            @Parameter(description = "주문ID") @PathVariable("id") UUID orderId
    ) throws Exception {
        service.cancelOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success("주문 취소 완료",orderId.toString()));
    }

    @PostMapping("{id}/sms/resend-ticket")
    @Operation(summary = "문자 재전송(관리자)", description = "관리자가 티켓정보를 재전송합니다.")
    public ResponseEntity<ApiResponse<String>> resendTicketSms(
            @Parameter(description = "주문ID") @PathVariable("id") UUID orderId
    ) throws Exception {
        service.resendTicketSms(orderId);
        return ResponseEntity.ok(ApiResponse.success("재전송 완료",orderId.toString()));
    }


}
