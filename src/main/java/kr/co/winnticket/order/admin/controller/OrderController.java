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
                "채널명", "파트너 이름", "마감일", "주문일", "주문번호", "회사명",
                "주문자 이름", "주문자 전화번호", "주문자 이메일",
                "수령자 이름", "수령자 전화번호",
                "상품번호", "주문상품", "티켓종류",
                "수량", "단가", "공급가", "총 주문금액",
                "결제금액", "결제수단",
                "무통장 결제금액", "신용카드 결제금액", "이용권", "베네피아 포인트 결제금액", "베네피아 아이디",
                "결제상태","결제일시","취소일시", "취소금액", "취소수수료", "환불수단", "티켓번호", "티켓사용여부", "소속사코드"
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
            row.createCell(2).setCellValue(first.getClosingDate() != null ? first.getClosingDate() : ""); // 마감일 (결제일 기준, 취소 시 취소일로 대체)
            row.createCell(3).setCellValue(first.getOrderedAt() != null ? first.getOrderedAt() : "");
            row.createCell(4).setCellValue(first.getOrderNumber() != null ? first.getOrderNumber() : "");
            row.createCell(5).setCellValue(first.getCompanyName() != null ? first.getCompanyName() : "");
            row.createCell(6).setCellValue(first.getCustomerName() != null ? first.getCustomerName() : "");
            row.createCell(7).setCellValue(formatPhoneNumber(first.getCustomerPhone()));
            row.createCell(8).setCellValue(first.getCustomerEmail() != null ? first.getCustomerEmail() : "");
            row.createCell(9).setCellValue(first.getRecipientName() != null ? first.getRecipientName() : "");
            row.createCell(10).setCellValue(formatPhoneNumber(first.getRecipientPhone()));
            row.createCell(11).setCellValue(productCodes);
            row.createCell(12).setCellValue(productNames);
            row.createCell(13).setCellValue(first.getTicketType() != null ? first.getTicketType() : "");
            row.createCell(14).setCellValue(totalQty);
            row.createCell(15).setCellValue(first.getUnitPrice() != null ? first.getUnitPrice() : 0);
            row.createCell(16).setCellValue(first.getSupplyPrice() != null ? first.getSupplyPrice() : 0);
            row.createCell(17).setCellValue(first.getTotalOrderAmount() != null ? first.getTotalOrderAmount() : 0);
            row.createCell(18).setCellValue(first.getFinalPrice() != null ? first.getFinalPrice() : 0);
            String pmDisplay = "";
            if (first.getPaymentMethod() != null) {
                try { pmDisplay = PaymentMethod.valueOf(first.getPaymentMethod()).getDisplayName(); }
                catch (Exception e) { pmDisplay = first.getPaymentMethod(); }
            }
            row.createCell(19).setCellValue(pmDisplay);
            row.createCell(20).setCellValue(first.getBankTransferAmount() != null ? first.getBankTransferAmount() : 0);
            row.createCell(21).setCellValue(first.getCardAmount() != null ? first.getCardAmount() : 0);
            row.createCell(22).setCellValue(first.getVoucherInfo() != null ? first.getVoucherInfo() : "");
            row.createCell(23).setCellValue(first.getPointAmount() != null ? first.getPointAmount() : 0);
            row.createCell(24).setCellValue(first.getBenepiaId() != null ? first.getBenepiaId() : "");
            String psDisplay = "";
            if (first.getPaymentStatus() != null) {
                try { psDisplay = PaymentStatus.valueOf(first.getPaymentStatus()).getDisplayName(); }
                catch (Exception e) { psDisplay = first.getPaymentStatus(); }
            }
            row.createCell(25).setCellValue(psDisplay);  // 결제 상태
            row.createCell(26).setCellValue(first.getPaidAt() != null ? first.getPaidAt() : ""); // 결제 일시
            row.createCell(27).setCellValue(first.getCanceledAt() != null ? first.getCanceledAt() : ""); // 결제취소일시
            row.createCell(28).setCellValue(first.getCancelAmount() != null ? first.getCancelAmount() : 0); // 취소금액 (마이너스)
            row.createCell(29).setCellValue(first.getCancelFee() != null ? first.getCancelFee() : 0); // 취소수수료 (항상 양수)
            // 환불수단 - 실제로 취소된 주문(취소일시가 있는 경우)만 결제수단과 동일하게 표시 (무통장/카드/포인트/이용권)
            boolean isCanceled = first.getCanceledAt() != null && !first.getCanceledAt().isEmpty();
            row.createCell(30).setCellValue(isCanceled ? pmDisplay : "");
            row.createCell(31).setCellValue(ticketNumbers);
            row.createCell(32).setCellValue(ticketUsedList);
            row.createCell(33).setCellValue(first.getSiteCode() != null ? first.getSiteCode() : "");
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

    // 베네피아 정산용 Excel 내보내기
    @GetMapping("/export/benepia-settlement")
    @Operation(summary = "베네피아 정산용 Excel 내보내기", description = "선택한 채널의 마감일자(결제완료일, 취소된 주문이면 취소일) 기준 기간에 대한 베네피아 정산 내역서를 Excel로 다운로드합니다.")
    public ResponseEntity<byte[]> exportBenepiaSettlementExcel(
            @Parameter(description = "채널 ID") @RequestParam("channelId") UUID channelId,
            @Parameter(description = "채널명 (파일명/시트 제목 표시용)") @RequestParam(value = "channelName", required = false) String channelName,
            @Parameter(description = "시작일 (마감일자 기준)") @RequestParam(value = "begDate", required = false) LocalDate begDate,
            @Parameter(description = "종료일 (마감일자 기준)") @RequestParam(value = "endDate", required = false) LocalDate endDate
    ) throws Exception {
        List<OrderBenepiaSettlementResDto> rows = service.selectBenepiaSettlementList(channelId, begDate, endDate);

        // 고정 수수료율 - 판매수수료(무통장/카드/이용권)와 복지포인트수수료(포인트 결제분에 추가로 붙음)
        final double SALES_FEE_RATE = 0.033;
        final double POINT_FEE_RATE = 0.022;

        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // ===== "order" 상세 시트 - 주문상품(아이템) 단위 =====
        HSSFSheet orderSheet = workbook.createSheet("order");
        String[] orderHeaders = {
                "주문일", "마감일자", "주문번호", "주문자 이름", "티켓종류", "결제수단",
                "SK 결제금액\n무통장결제", "SK 결제금액\n카드결제", "SK 결제금액\n포인트결제", "SK 결제금액\n이용권결제",
                "SK 수수료\n무통장결제", "SK 수수료\n카드결제", "SK 수수료\n포인트결제", "SK 수수료\n이용권결제",
                "상품별\n결제금액", "개별판매가", "주문상품", "수량", "카테고리",
                "총 결제금액", "무통장", "카드", "포인트", "이용권", "결제금액", "취소금액", "취소수수료",
                "결제일시", "취소접수", "취소완료", "주문상태", "소속사코드"
        };
        HSSFRow orderHeaderRow = orderSheet.createRow(2);
        for (int i = 0; i < orderHeaders.length; i++) {
            HSSFCell cell = orderHeaderRow.createCell(i);
            cell.setCellValue(orderHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        // 주문 단위 합계 집계용 (요약 시트에서 사용 - 아이템 반복 때문에 중복 합산되지 않도록 주문당 1건만 보관)
        java.util.Map<UUID, OrderBenepiaSettlementResDto> orderTotals = new java.util.LinkedHashMap<>();

        int rowNum = 3;
        for (OrderBenepiaSettlementResDto r : rows) {
            orderTotals.putIfAbsent(r.getOrderId(), r);

            // 상품별 결제금액 - 취소된 주문이면 수량이 음수이므로 자연히 마이너스로 표시됨
            int lineTotal = (r.getUnitPrice() != null ? r.getUnitPrice() : 0) * (r.getQuantity() != null ? r.getQuantity() : 0);
            // 주문의 무통장/카드/포인트/이용권 금액을 이 상품이 차지하는 비율(상품별 결제금액 / 총 결제금액)만큼 배분.
            // lineTotal이 이미 취소 시 마이너스이므로 ratio 자체가 부호를 그대로 갖고 내려간다.
            double ratio = (r.getFinalPrice() != null && r.getFinalPrice() != 0)
                    ? (double) lineTotal / r.getFinalPrice() : 0;

            // 취소 시 무통장/카드는 취소수수료가 빠진 실제 환불액(취소금액)만큼만 SK 결제금액에 반영되어야
            // 하므로, 원금 대비 실제 환불 비율(취소금액 / (무통장+카드))을 추가로 곱해준다.
            // 포인트/이용권은 취소금액에 포함되지 않고 별도로 전액 환급되므로 그대로 둔다.
            double bankCardGross = (r.getBankAmount() != null ? r.getBankAmount() : 0)
                    + (r.getCardAmount() != null ? r.getCardAmount() : 0);
            double feeAdjustFactor = 1.0;
            if ("CANCELED".equals(r.getStatus()) && bankCardGross != 0 && r.getCancelAmount() != null) {
                feeAdjustFactor = Math.abs(r.getCancelAmount()) / bankCardGross;
            }

            double bankAlloc = (r.getBankAmount() != null ? r.getBankAmount() : 0) * ratio * feeAdjustFactor;
            double cardAlloc = (r.getCardAmount() != null ? r.getCardAmount() : 0) * ratio * feeAdjustFactor;
            double pointAlloc = (r.getPointAmount() != null ? r.getPointAmount() : 0) * ratio;
            double voucherAlloc = (r.getVoucherAmount() != null ? r.getVoucherAmount() : 0) * ratio;

            String pmDisplay = "";
            if (r.getPaymentMethod() != null) {
                try { pmDisplay = PaymentMethod.valueOf(r.getPaymentMethod()).getDisplayName(); }
                catch (Exception e) { pmDisplay = r.getPaymentMethod(); }
            }
            String statusDisplay = "";
            if (r.getStatus() != null) {
                try { statusDisplay = kr.co.winnticket.common.enums.OrderStatus.valueOf(r.getStatus()).getDisplayName(); }
                catch (Exception e) { statusDisplay = r.getStatus(); }
            }

            HSSFRow row = orderSheet.createRow(rowNum++);
            row.createCell(0).setCellValue(r.getOrderedAt() != null ? r.getOrderedAt() : "");
            row.createCell(1).setCellValue(r.getClosingDate() != null ? r.getClosingDate() : "");
            row.createCell(2).setCellValue(r.getOrderNumber() != null ? r.getOrderNumber() : "");
            row.createCell(3).setCellValue(r.getCustomerName() != null ? r.getCustomerName() : "");
            row.createCell(4).setCellValue("모바일자동");
            row.createCell(5).setCellValue(pmDisplay);
            row.createCell(6).setCellValue(bankAlloc);
            row.createCell(7).setCellValue(cardAlloc);
            row.createCell(8).setCellValue(pointAlloc);
            row.createCell(9).setCellValue(voucherAlloc);
            // SK 수수료는 SK 결제금액(위 무통장/카드/포인트/이용권 배분액)에 대한 비율이므로
            // 그 금액과 같은 부호를 가져야 함 - 환불(마이너스)이면 수수료도 마이너스(환급)
            row.createCell(10).setCellValue(bankAlloc * SALES_FEE_RATE);
            row.createCell(11).setCellValue(cardAlloc * SALES_FEE_RATE);
            row.createCell(12).setCellValue(pointAlloc * (SALES_FEE_RATE + POINT_FEE_RATE));
            row.createCell(13).setCellValue(voucherAlloc * SALES_FEE_RATE);
            row.createCell(14).setCellValue(lineTotal);
            row.createCell(15).setCellValue(r.getUnitPrice() != null ? r.getUnitPrice() : 0);
            row.createCell(16).setCellValue(r.getProductDisplayName() != null ? r.getProductDisplayName() : "");
            row.createCell(17).setCellValue(r.getQuantity() != null ? r.getQuantity() : 0);
            row.createCell(18).setCellValue(r.getCategoryName() != null ? r.getCategoryName() : "");
            row.createCell(19).setCellValue(r.getFinalPrice() != null ? r.getFinalPrice() : 0);
            row.createCell(20).setCellValue(r.getBankAmount() != null ? r.getBankAmount() : 0);
            row.createCell(21).setCellValue(r.getCardAmount() != null ? r.getCardAmount() : 0);
            row.createCell(22).setCellValue(r.getPointAmount() != null ? r.getPointAmount() : 0);
            row.createCell(23).setCellValue(r.getVoucherAmount() != null ? r.getVoucherAmount() : 0);
            row.createCell(24).setCellValue(r.getFinalPrice() != null ? r.getFinalPrice() : 0);
            row.createCell(25).setCellValue(r.getCancelAmount() != null ? r.getCancelAmount() : 0);
            row.createCell(26).setCellValue(r.getCancelFee() != null ? r.getCancelFee() : 0);
            row.createCell(27).setCellValue(r.getPaidAt() != null ? r.getPaidAt() : "");
            row.createCell(28).setCellValue(r.getCancelRequestedAt() != null ? r.getCancelRequestedAt() : "");
            row.createCell(29).setCellValue(r.getCanceledAt() != null ? r.getCanceledAt() : "");
            row.createCell(30).setCellValue(statusDisplay);
            row.createCell(31).setCellValue(r.getSiteCode() != null ? r.getSiteCode() : "");
        }

        for (int i = 0; i < orderHeaders.length; i++) {
            orderSheet.autoSizeColumn(i);
        }

        // ===== "-" 요약 시트 =====
        HSSFSheet summarySheet = workbook.createSheet("-");
        workbook.setSheetOrder("-", 0);

        long totalPoint = 0;
        long totalBankCard = 0;
        for (OrderBenepiaSettlementResDto o : orderTotals.values()) {
            // 취소된 주문은 상세 시트에서 마이너스로 표시되므로, 정산 합계에서도 차감되어야 함
            int sign = "CANCELED".equals(o.getStatus()) ? -1 : 1;
            totalPoint += sign * (o.getPointAmount() != null ? o.getPointAmount() : 0);
            totalBankCard += sign * ((o.getBankAmount() != null ? o.getBankAmount() : 0)
                    + (o.getCardAmount() != null ? o.getCardAmount() : 0));
        }
        // 판매수수료(3.3%)는 무통장/카드 금액에, 복지포인트수수료(3.3%+2.2%=5.5%)는 포인트 금액에
        // 적용 - "order" 상세 시트의 SK 수수료 컬럼과 합계가 일치하도록 함.
        // 부호는 각 금액과 같이 가짐 - 취소로 인해 순감(마이너스)이면 수수료도 마이너스(환급)로 표시
        double salesFee = totalBankCard * SALES_FEE_RATE;
        double pointFee = totalPoint * (SALES_FEE_RATE + POINT_FEE_RATE);
        double totalFee = salesFee + pointFee;
        double payoutAmount = totalPoint - totalFee;

        HSSFCellStyle titleStyle = workbook.createCellStyle();
        HSSFFont titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);

        HSSFRow titleRow = summarySheet.createRow(1);
        HSSFCell titleCell = titleRow.createCell(1);
        titleCell.setCellValue((channelName != null && !channelName.isBlank() ? channelName : "") + "_베네피아 정산 내역서");
        titleCell.setCellStyle(titleStyle);

        summarySheet.createRow(3).createCell(1)
                .setCellValue(endDate != null ? endDate.toString() : LocalDate.now().toString());

        HSSFRow headRow = summarySheet.createRow(4);
        String[] summaryHeaders = {"구분", "수수료(vat포함)", "kcp 결제", "KCP 외 타 결제", "총 결제액", "수수료"};
        for (int i = 0; i < summaryHeaders.length; i++) {
            HSSFCell cell = headRow.createCell(i + 2);
            cell.setCellValue(summaryHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        HSSFRow subHeadRow = summarySheet.createRow(5);
        subHeadRow.createCell(4).setCellValue("복지포인트");
        subHeadRow.createCell(5).setCellValue("카드/무통장");

        HSSFRow row7 = summarySheet.createRow(6);
        row7.createCell(1).setCellValue("kcp 결제\n(복지포인트) ");
        row7.createCell(2).setCellValue("판매수수료");
        row7.createCell(3).setCellValue("3.3%");
        row7.createCell(4).setCellValue(totalPoint);
        row7.createCell(5).setCellValue(totalBankCard);
        row7.createCell(6).setCellValue(totalPoint + totalBankCard);
        row7.createCell(7).setCellValue(salesFee);

        HSSFRow row8 = summarySheet.createRow(7);
        row8.createCell(2).setCellValue("복지포인트수수료");
        row8.createCell(3).setCellValue("5.5%");
        row8.createCell(4).setCellValue(totalPoint);
        row8.createCell(6).setCellValue(totalPoint);
        row8.createCell(7).setCellValue(pointFee);

        HSSFRow row9 = summarySheet.createRow(8);
        row9.createCell(2).setCellValue("수수료 세금계산서 발행액");
        row9.createCell(7).setCellValue(totalFee);

        HSSFRow row10 = summarySheet.createRow(9);
        row10.createCell(2).setCellValue("복지포인트 지급액 (=청구액)");
        row10.createCell(7).setCellValue(payoutAmount);

        // 정산 은행/계좌 - 회사 고정 계좌 (필요 시 값만 교체하면 됨)
        HSSFRow row12 = summarySheet.createRow(11);
        row12.createCell(1).setCellValue("정산 은행");
        row12.createCell(3).setCellValue("하나은행");

        HSSFRow row13 = summarySheet.createRow(12);
        row13.createCell(1).setCellValue("정산 계좌");
        row13.createCell(3).setCellValue("47691002132304");

        for (int i = 0; i <= 7; i++) {
            summarySheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        String safeChannelName = (channelName != null && !channelName.isBlank()) ? channelName : "채널";
        String filename = safeChannelName + "_베네피아_정산내역서_"
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xls";
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

    @PostMapping("{id}/cancel-request/withdraw")
    @Operation(summary = "취소신청 철회(관리자)", description = "고객이 요청한 취소를 관리자가 반려하고 주문을 주문처리완료 상태로 되돌립니다.")
    public ResponseEntity<ApiResponse<String>> withdrawCancelRequest(
            @Parameter(description = "주문ID") @PathVariable("id") UUID orderId
    ) throws Exception {
        service.withdrawCancelRequest(orderId);
        return ResponseEntity.ok(ApiResponse.success("취소신청이 철회되었습니다.", orderId.toString()));
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
