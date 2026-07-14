package kr.co.winnticket.order.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.common.enums.PaymentMethod;
import kr.co.winnticket.common.enums.PaymentStatus;
import kr.co.winnticket.order.admin.dto.*;
import kr.co.winnticket.order.admin.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
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
            @Parameter(description = "채널Id") @RequestParam(value = "channelId", required = false) UUID channelId,
            @Parameter(description = "파트너Id") @RequestParam(value = "partnerId", required = false) UUID partnerId,
            @Parameter(description = "파트너명") @RequestParam(value = "partnerName", required = false) String partnerName
    ) throws Exception {
        List<OrderExportResDto> rows = service.selectOrderExportList(asSrchWord, asBegDate, asEndDate, status, channelId, partnerId, partnerName);

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
                "채널명", "파트너 이름", "주문일", "주문번호", "회사명",
                "주문자 이름", "주문자 전화번호", "주문자 이메일",
                "수령자 이름", "수령자 전화번호",
                "상품번호", "주문상품", "티켓종류",
                "수량", "단가", "공급가", "총 주문금액",
                "결제금액", "결제수단",
                "베네피아 포인트 결제금액", "베네피아 아이디",
                "무통장 결제금액", "신용카드 결제금액", "이용권",
                "결제상태","결제일시","취소일시", "티켓번호", "티켓사용여부"
        };

        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 같은 주문번호끼리 그룹핑하여 한 줄로 합침 (상품명은 / 로 연결)
        java.util.LinkedHashMap<String, java.util.List<OrderExportResDto>> grouped = new java.util.LinkedHashMap<>();
        for (OrderExportResDto r : rows) {
            grouped.computeIfAbsent(r.getOrderNumber(), k -> new java.util.ArrayList<>()).add(r);
        }

        int rowNum = 1;
        for (java.util.Map.Entry<String, java.util.List<OrderExportResDto>> entry : grouped.entrySet()) {
            java.util.List<OrderExportResDto> items = entry.getValue();
            OrderExportResDto first = items.get(0);

            String productNames = items.stream()
                    .map(i -> i.getProductDisplayName() != null ? i.getProductDisplayName() : "")
                    .collect(java.util.stream.Collectors.joining("/"));
            String productCodes = items.stream()
                    .map(i -> i.getProductCode() != null ? i.getProductCode() : "")
                    .collect(java.util.stream.Collectors.joining("/"));
            int totalQty = items.stream().mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum();
            String ticketNumbers = items.stream()
                    .map(i -> i.getTicketNumber() != null ? i.getTicketNumber() : "")
                    .filter(s -> !s.isEmpty())
                    .collect(java.util.stream.Collectors.joining(", "));
            String ticketUsedList = items.stream()
                    .map(i -> i.getTicketUsed() != null ? i.getTicketUsed() : "미사용")
                    .collect(java.util.stream.Collectors.joining(", "));

            HSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(first.getChannelName() != null ? first.getChannelName() : "");
            row.createCell(1).setCellValue(first.getPartnerName() != null ? first.getPartnerName() : "");
            row.createCell(2).setCellValue(first.getOrderedAt() != null ? first.getOrderedAt() : "");
            row.createCell(3).setCellValue(first.getOrderNumber() != null ? first.getOrderNumber() : "");
            row.createCell(4).setCellValue(first.getCompanyName() != null ? first.getCompanyName() : "");
            row.createCell(5).setCellValue(first.getCustomerName() != null ? first.getCustomerName() : "");
            row.createCell(6).setCellValue(formatPhoneNumber(first.getCustomerPhone()));
            row.createCell(7).setCellValue(first.getCustomerEmail() != null ? first.getCustomerEmail() : "");
            row.createCell(8).setCellValue(first.getRecipientName() != null ? first.getRecipientName() : "");
            row.createCell(9).setCellValue(formatPhoneNumber(first.getRecipientPhone()));
            row.createCell(10).setCellValue(productCodes);
            row.createCell(11).setCellValue(productNames);
            row.createCell(12).setCellValue(first.getTicketType() != null ? first.getTicketType() : "");
            row.createCell(13).setCellValue(totalQty);
            row.createCell(14).setCellValue(first.getUnitPrice() != null ? first.getUnitPrice() : 0);
            row.createCell(15).setCellValue(first.getSupplyPrice() != null ? first.getSupplyPrice() : 0);
            row.createCell(16).setCellValue(first.getTotalOrderAmount() != null ? first.getTotalOrderAmount() : 0);
//            String psDisplay = "";
//            if (first.getPaymentStatus() != null) {
//                try { psDisplay = PaymentStatus.valueOf(first.getPaymentStatus()).getDisplayName(); }
//                catch (Exception e) { psDisplay = first.getPaymentStatus(); }
//            }
//            row.createCell(17).setCellValue(psDisplay);
            row.createCell(17).setCellValue(first.getFinalPrice() != null ? first.getFinalPrice() : 0);
            String pmDisplay = "";
            if (first.getPaymentMethod() != null) {
                try { pmDisplay = PaymentMethod.valueOf(first.getPaymentMethod()).getDisplayName(); }
                catch (Exception e) { pmDisplay = first.getPaymentMethod(); }
            }
            row.createCell(18).setCellValue(pmDisplay);
            row.createCell(19).setCellValue(first.getPointAmount() != null ? first.getPointAmount() : 0);
            row.createCell(20).setCellValue(first.getBenepiaId() != null ? first.getBenepiaId() : "");
            row.createCell(21).setCellValue(first.getBankTransferAmount() != null ? first.getBankTransferAmount() : 0);
            row.createCell(22).setCellValue(first.getCardAmount() != null ? first.getCardAmount() : 0);
            row.createCell(23).setCellValue("");
            String psDisplay = "";
            if (first.getPaymentStatus() != null) {
                try { psDisplay = PaymentStatus.valueOf(first.getPaymentStatus()).getDisplayName(); }
                catch (Exception e) { psDisplay = first.getPaymentStatus(); }
            }
            row.createCell(24).setCellValue(psDisplay);  // 결제 상태
            row.createCell(25).setCellValue(first.getPaidAt() != null ? first.getPaidAt() : ""); // 결제 일시
            row.createCell(26).setCellValue(first.getCanceledAt() != null ? first.getCanceledAt() : ""); // 결제취소일시
            row.createCell(27).setCellValue(ticketNumbers);
            row.createCell(28).setCellValue(ticketUsedList);
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

    private String formatPhoneNumber(String phone) {

        if (phone == null || phone.isBlank()) {
            return "";
        }

        String onlyNumber = phone.replaceAll("[^0-9]", "");

        // 01012341234 -> 010-1234-1234
        if (onlyNumber.length() == 11) {
            return onlyNumber.replaceFirst(
                    "(\\d{3})(\\d{4})(\\d{4})",
                    "$1-$2-$3"
            );
        }

        // 0212345678 -> 021-234-5678
        if (onlyNumber.length() == 10) {
            return onlyNumber.replaceFirst(
                    "(\\d{3})(\\d{3})(\\d{4})",
                    "$1-$2-$3"
            );
        }

        return phone;
    }


}
