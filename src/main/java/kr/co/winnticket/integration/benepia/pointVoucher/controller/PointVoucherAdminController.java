package kr.co.winnticket.integration.benepia.pointVoucher.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherAdminDetailResDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherAdminListResDto;
import kr.co.winnticket.integration.benepia.pointVoucher.service.PointVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
