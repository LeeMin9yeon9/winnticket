package kr.co.winnticket.ticketCoupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.ticketCoupon.dto.TicketCouponCreateReqDto;
import kr.co.winnticket.ticketCoupon.dto.TicketCouponGroupResDto;
import kr.co.winnticket.ticketCoupon.dto.TicketCouponListResDto;
import kr.co.winnticket.ticketCoupon.dto.TicketCouponUpdateReqDto;
import kr.co.winnticket.ticketCoupon.service.TicketCouponService;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/ticketCoupon")
@Tag(name = "ADMIN 선사입 티켓쿠폰", description = "")
public class TicketCouponController {

    private final TicketCouponService service;

    @Operation(summary = "쿠폰 생성(선사입형)", description = "옵션값 기준 그룹 생성 후 쿠폰번호 범위 생성")
    @PostMapping("/groups/coupons")
    public ResponseEntity<ApiResponse<TicketCouponCreateReqDto>> create(@RequestBody TicketCouponCreateReqDto dto) {
        String resultMessage = service.createCoupons(dto);
        return ResponseEntity.ok(ApiResponse.success(resultMessage, dto));
    }

    @Operation(summary = "쿠폰그룹 목록", description = "그룹 목록 + ACTIVE/USED 카운트")
    @GetMapping("/products/{productId}/groups")
    public ResponseEntity<ApiResponse<List<TicketCouponGroupResDto>>> groups(
            @Parameter(description = "상품ID") @PathVariable UUID productId) {

        return ResponseEntity.ok(ApiResponse.success("조회 성공", service.getGroups(productId)));
    }

    @Operation(summary = "쿠폰그룹 단건", description = "그룹 단건 + ACTIVE/USED 카운트")
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<ApiResponse<TicketCouponGroupResDto>> group(
            @Parameter(description = "그룹ID") @PathVariable UUID groupId) {

        return ResponseEntity.ok(ApiResponse.success("조회 성공", service.getGroup(groupId)));
    }

    @Operation(summary = "그룹별 쿠폰 목록", description = "groupId로 쿠폰 리스트 조회")
    @GetMapping("/groups/{groupId}/coupons")
    public ResponseEntity<ApiResponse<List<TicketCouponListResDto>>> couponsByGroup(
            @Parameter(description = "그룹ID") @PathVariable UUID groupId) {

        return ResponseEntity.ok(ApiResponse.success("조회 성공", service.getCouponsByGroup(groupId)));
    }

    @Operation(summary = "쿠폰 단건 조회", description = "couponId로 쿠폰 단건 조회")
    @GetMapping("/coupons/{couponId}")
    public ResponseEntity<ApiResponse<TicketCouponListResDto>> coupon(
            @Parameter(description = "쿠폰ID") @PathVariable UUID couponId) {

        return ResponseEntity.ok(ApiResponse.success("조회 성공", service.getCoupon(couponId)));
    }


    @Operation(summary = "쿠폰 수정", description = "쿠폰번호/상태/사용일자/유효기간 수정 가능")
    @PatchMapping("/coupons/{couponId}")
    public ResponseEntity<ApiResponse<Void>> updateCoupon(
            @Parameter(description = "쿠폰ID") @PathVariable UUID couponId,
            @RequestBody TicketCouponUpdateReqDto dto) {

        service.updateCoupon(couponId, dto);
        return ResponseEntity.ok(ApiResponse.success("수정 성공", null));
    }

    @Operation(summary = "쿠폰그룹 수정", description = "그룹명/유효기간 수정")
    @PatchMapping("/groups/{groupId}")
    public ResponseEntity<ApiResponse<Void>> updateGroup(
            @Parameter(description = "그룹ID") @PathVariable UUID groupId,
            @RequestParam(required = false) LocalDate validFrom,
            @RequestParam(required = false) LocalDate validUntil) {

        service.updateGroup(groupId, validFrom, validUntil);
        return ResponseEntity.ok(ApiResponse.success("수정 성공", null));
    }


    @Operation(summary = "쿠폰 삭제", description = "couponId로 쿠폰 삭제")
    @DeleteMapping("/coupons/{couponId}")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(
            @Parameter(description = "쿠폰ID") @PathVariable UUID couponId) {

        service.deleteCoupon(couponId);
        return ResponseEntity.ok(ApiResponse.success("삭제 성공", null));
    }

    @Operation(summary = "쿠폰그룹 삭제", description = "groupId로 그룹 삭제(쿠폰 CASCADE이면 같이 삭제)")
    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @Parameter(description = "그룹ID") @PathVariable UUID groupId) {

        service.deleteGroup(groupId);
        return ResponseEntity.ok(ApiResponse.success("삭제 성공", null));
    }

    @Operation(summary = "쿠폰 발급", description = "구매 시 사용가능 쿠폰을 SOLD 상태로 변경하고 쿠폰번호 반환")
    @PostMapping("/issue/{orderItemId}")
    public ResponseEntity<ApiResponse<String>> issueCoupon(
            @PathVariable UUID orderItemId
    ) {

        String couponNumber = service.issueCoupon(orderItemId, null, null);

        return ResponseEntity.ok(ApiResponse.success("쿠폰 발급 성공", couponNumber)
        );
    }

    @Operation(summary = "쿠폰 취소 복구",description = "사용되지 않은 쿠폰을 ACTIVE 상태로 복구")
    @PostMapping("/usedCancel/{couponId}")
    public ResponseEntity<ApiResponse<Void>> cancelCoupon(
            @PathVariable UUID couponId){

        service.cancelCoupon(couponId);

        return ResponseEntity.ok(ApiResponse.success("쿠폰 복구 완료", null)
        );
    }

    @PutMapping("/group/date")
    @Operation(summary = "쿠폰 그룹 날짜 변경", description = "해당 그룹 전체 쿠폰 날짜 일괄 변경")

    public ResponseEntity<ApiResponse<Void>> updateGroupDate(
            @Parameter(description="그룹ID") @RequestParam UUID groupId,
            @Parameter(description="사용 시작일") @RequestParam LocalDate validFrom,
            @Parameter(description="사용 종료일") @RequestParam LocalDate validUntil
    ){
        service.updateGroupDate(
                groupId,
                validFrom,
                validUntil
        );

        return ResponseEntity.ok(ApiResponse.<Void>success("변경 완료", null)
        );
    }

    @GetMapping("/groups/{groupId}/coupons/export")
    @Operation(summary = "쿠폰 목록 Excel 다운로드", description = "그룹별 쿠폰 목록 Excel 다운로드")
    public ResponseEntity<byte[]> exportCoupons(
            @PathVariable UUID groupId
    ) throws Exception {

        List<TicketCouponListResDto> rows = service.getCouponsByGroup(groupId);

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("쿠폰목록");

        HSSFCellStyle headerStyle = workbook.createCellStyle();
        HSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        String[] headers = {
                "쿠폰번호",
                "주문번호",
                "주문자명",
                "상태",
                "유효시작일",
                "유효종료일",
                "생성일",
                "사용일"
        };

        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (TicketCouponListResDto r : rows) {
            HSSFRow row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(
                    r.getCouponNumber() != null ? r.getCouponNumber() : ""
            );

            row.createCell(1).setCellValue(
                    r.getOrderNumber() != null ? r.getOrderNumber() : ""
            );

            row.createCell(2).setCellValue(
                    r.getCustomerName() != null ? r.getCustomerName() : ""
            );

            row.createCell(3).setCellValue(
                    convertCouponStatus(r.getStatus())
            );

            row.createCell(4).setCellValue(
                    r.getValidFrom() != null ? r.getValidFrom().toString() : ""
            );

            row.createCell(5).setCellValue(
                    r.getValidUntil() != null ? r.getValidUntil().toString() : ""
            );

            row.createCell(6).setCellValue(
                    r.getCreatedAt() != null ? r.getCreatedAt().toString() : ""
            );

            row.createCell(7).setCellValue(

                    r.getUsedAt() != null ? r.getUsedAt().toString() : ""
            );
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        String productName = rows.isEmpty() ? "쿠폰목록" : rows.get(0).getProductName();

        productName = productName.replaceAll("[\\\\/:*?\"<>|]", "_");

        String filename = productName + "_쿠폰목록.xls";
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedFilename
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(baos.toByteArray());
    }

    private String convertCouponStatus(String status) {
        if (status == null) return "";

        return switch (status) {
            case "ACTIVE" -> "사용가능";
            case "SOLD" -> "판매됨";
            case "USED" -> "사용완료";
            default -> status;
        };
    }


}





