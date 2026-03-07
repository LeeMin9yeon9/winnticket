package kr.co.winnticket.integration.mair.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.co.winnticket.auth.config.ApiResDto;
import kr.co.winnticket.integration.common.IntegrationResult;
import kr.co.winnticket.integration.mair.dto.MairCouponResDto;
import kr.co.winnticket.integration.mair.service.MairService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mair")
public class MairController {

    private final MairService service;

    // 발송 테스트
    @PostMapping("/issue/{orderNumber}")
    @Operation(summary = "엠에어 쿠폰 발송", description = "엠에어 쿠폰 발송요청 API")
    public ResponseEntity<ApiResDto<List<MairCouponResDto>>> issuePost(@PathVariable String orderNumber) {

        log.info("[MAIR][ISSUE] orderNumber={}", orderNumber);

        List<MairCouponResDto> result = service.issueTickets(orderNumber);

        return ResponseEntity.ok( ApiResDto.success("엠에어 쿠폰 발송 성공", result)
        );
    }

    // 취소 테스트
    @PostMapping("/cancel/{orderNumber}")
    @Operation(summary = " 엠에어 취소 ", description = "엠에어 쿠폰 취소요청 API")
    public  ResponseEntity<ApiResDto<List<IntegrationResult>>> cancelPost(@PathVariable String orderNumber) {
        log.info("[MAIR][CANCEL] orderNumber={}", orderNumber);

        List<IntegrationResult> result = service.cancelByOrder(orderNumber);

        return ResponseEntity.ok(ApiResDto.success("엠에어 쿠폰 취소 성공", result));
    }

    //
    @GetMapping("/use-check/{orderNumber}")
    @Operation(summary = "엠에어 사용여부 확인", description = "엠에어 쿠폰사용여부 확인")
    public ResponseEntity<ApiResDto<IntegrationResult>> useCheck(@PathVariable String orderNumber) {

        log.info("[MAIR][USE-CHECK] orderNumber={}", orderNumber);

        IntegrationResult result = service.useCheckByOrderNumber(orderNumber);

        return ResponseEntity.ok(ApiResDto.success("엠에어 사용 여부 조회 완료",result));
    }
}
