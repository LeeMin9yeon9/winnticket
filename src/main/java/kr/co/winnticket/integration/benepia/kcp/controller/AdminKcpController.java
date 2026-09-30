package kr.co.winnticket.integration.benepia.kcp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpModResDto;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointCancelReqDto;
import kr.co.winnticket.integration.benepia.kcp.service.KcpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// /api/admin/** 는 SecurityConfig에서 ROLE001(관리자)만 호출 가능하도록 이미 강제되어 있음.
// 정상 결제/취소 플로우는 전부 서버 내부에서 KcpService를 직접 호출하고, 여기 있는 엔드포인트는
// tno 불일치 등으로 KCP 쪽과 대사가 어긋났을 때 관리자가 수기로 바로잡기 위한 용도로만 사용한다.
@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/admin/kcp")
@Tag(name = "관리자 KCP", description = "관리자 전용 KCP 포인트 수동 취소(데이터 정합성 대사용)")
public class AdminKcpController {

    private final KcpService service;

    @PostMapping("/cancel")
    @Operation(summary = "KCP 포인트 수동 취소", description = "관리자가 특정 tno를 지정해 KCP 포인트 결제를 직접 취소합니다.")
    public ResponseEntity<ApiResponse<KcpModResDto>> cancel(
            @Valid @RequestBody KcpPointCancelReqDto dto
    ) {
        log.info("[ADMIN][KCP][CANCEL][REQ] tno={}, modType={}, reason={}",
                dto.getTno(), dto.getModType(), dto.getCancelReason());

        KcpModResDto res = service.cancelPoint(dto);

        return ResponseEntity.ok(ApiResponse.success("포인트 취소 성공", res));
    }
}
