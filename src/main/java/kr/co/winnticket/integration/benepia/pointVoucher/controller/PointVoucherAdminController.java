package kr.co.winnticket.integration.benepia.pointVoucher.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import jakarta.validation.Valid;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherAdminDetailResDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherAdminListResDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherManualCreateReqDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherRemainingAmountUpdateReqDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherValidUntilUpdateReqDto;
import kr.co.winnticket.integration.benepia.pointVoucher.service.PointVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/benepia/voucher")
@Tag(name = "이용권 - 관리자", description = "발급된 이용권 목록/상세 조회 및 취소. 관리자 JWT(ROLE001) 필요")
public class PointVoucherAdminController {

    private final PointVoucherService pointVoucherService;

    @Operation(summary = "이용권 수동 등록", description = "관리자가 이용권을 직접 등록합니다. 실제 KCP 포인트 차감 없이 이용권 레코드만 생성됩니다. (타 시스템 이관, 고객 응대 목적 등)")
    @PostMapping
    public ResponseEntity<ApiResponse<PointVoucherAdminDetailResDto>> create(
            @Valid @RequestBody PointVoucherManualCreateReqDto reqDto) {
        PointVoucherAdminDetailResDto result = pointVoucherService.manualCreate(reqDto);
        return ResponseEntity.ok(ApiResponse.success("이용권이 등록되었습니다.", result));
    }

    @Operation(summary = "이용권 목록 조회", description = "이름/베네피아아이디/휴대폰번호/이용권번호 통합검색 + 상태 필터")
    @GetMapping
    public ResponseEntity<ApiResponse<List<PointVoucherAdminListResDto>>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        List<PointVoucherAdminListResDto> result = pointVoucherService.adminList(keyword, status);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "이용권 상세 조회", description = "이용권 정보 + 사용내역 목록 + 취소 가능 여부/기한")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PointVoucherAdminDetailResDto>> detail(@PathVariable UUID id) {
        PointVoucherAdminDetailResDto result = pointVoucherService.adminDetail(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "이용권 취소", description = "미사용 이용권만, 채널에 설정된 취소가능기간 이내에 취소 가능. 취소 시 차감된 포인트도 KCP로 전액 환불됨.")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable UUID id) {
        pointVoucherService.cancelVoucher(id);
        return ResponseEntity.ok(ApiResponse.success("이용권이 취소되었습니다.", null));
    }

    @Operation(summary = "이용권 사용기한 변경", description = "ACTIVE 상태 이용권만 가능. 이미 기한이 지난 이용권도 연장하면 즉시 다시 사용 가능해짐.")
    @PatchMapping("/{id}/valid-until")
    public ResponseEntity<ApiResponse<Void>> updateValidUntil(
            @PathVariable UUID id,
            @Valid @RequestBody PointVoucherValidUntilUpdateReqDto reqDto) {
        pointVoucherService.updateValidUntil(id, reqDto.getValidUntil());
        return ResponseEntity.ok(ApiResponse.success("이용권 사용기한이 변경되었습니다.", null));
    }

    @Operation(summary = "이용권 잔여금액 변경", description = "ACTIVE 상태 이용권만 가능. 사용이력(used_amount)은 유지한 채 잔여금액과 총액을 재계산.")
    @PatchMapping("/{id}/remaining-amount")
    public ResponseEntity<ApiResponse<Void>> updateRemainingAmount(
            @PathVariable UUID id,
            @Valid @RequestBody PointVoucherRemainingAmountUpdateReqDto reqDto) {
        pointVoucherService.updateRemainingAmount(id, reqDto.getRemainingAmount());
        return ResponseEntity.ok(ApiResponse.success("이용권 잔여금액이 변경되었습니다.", null));
    }
}
