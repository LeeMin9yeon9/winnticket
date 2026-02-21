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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        service.createCoupons(dto);
        return ResponseEntity.ok(ApiResponse.success("생성 성공", dto));
    }

    @Operation(summary = "쿠폰그룹 목록", description = "그룹 목록 + ACTIVE/USED 카운트")
    @GetMapping("/groups")
    public ResponseEntity<ApiResponse<List<TicketCouponGroupResDto>>> groups() {
        return ResponseEntity.ok(ApiResponse.success("조회 성공", service.getGroups()));
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

        String couponNumber = service.issueCoupon(orderItemId);

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


}





