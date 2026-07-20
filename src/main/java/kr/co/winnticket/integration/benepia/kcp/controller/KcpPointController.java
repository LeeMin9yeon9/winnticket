package kr.co.winnticket.integration.benepia.kcp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.integration.benepia.kcp.dto.*;
import kr.co.winnticket.integration.benepia.kcp.service.KcpService;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/benepia/kcp")
@Tag(name = "베네피아 KCP", description = "베네피아 KCP 포인트")
public class KcpPointController {

    private final KcpService service;

    // 클라이언트가 보낸 값은 무시하고, SSO 진입 시 복호화된 sitecode(회원 소속사코드)로 덮어씀 - 위변조 방지
    private String resolveMemcorpCd(HttpSession session) {
        BenepiaDecryptedParamDto decrypted = (BenepiaDecryptedParamDto) session.getAttribute("BENEP_DECRYPTED");
        if (decrypted == null || !StringUtils.hasText(decrypted.getSitecode())) {
            throw new IllegalStateException("베네피아 웹 진입 후 호출해야 함");
        }
        return decrypted.getSitecode();
    }

    @PostMapping("/point")
    @Operation(summary = "베네피아 KCP 포인트 조회", description = "베네피아 계정으로 KCP 포인트를 조회합니다.")
    public ResponseEntity<ApiResponse<KcpPointResDto>> point(
            @Valid @RequestBody KcpPointReqDto dto,
            HttpSession session
    ) {

        dto.setMemcorpCd(resolveMemcorpCd(session));

        log.info("[KCP][POINT][REQ] orderNo={}, amount={}, benepiaId={}, memcorpCd={}",
                dto.getOrderNo(), dto.getAmount(), dto.getBenepiaId(), dto.getMemcorpCd());

        KcpPointResDto res = service.getPoint(dto);

        return ResponseEntity.ok(ApiResponse.success("포인트 조회 성공", res));
    }

    @PostMapping("/pay")
    @Operation(summary = "베네피아 KCP 포인트 결제", description = "베네피아 포인트로 결제를 승인합니다.")
    public ResponseEntity<ApiResponse<KcpPointPayResDto>> pay(
            @Valid @RequestBody KcpPointPayReqDto dto,
            HttpSession session
    ) {

        dto.setMemcorpCd(resolveMemcorpCd(session));

        if (!StringUtils.hasText(dto.getBuyerPhone())) dto.setBuyerPhone(null);
        if (!StringUtils.hasText(dto.getBuyerEmail())) dto.setBuyerEmail(null);

        log.info("[KCP][PAY][REQ] orderNo={}, amount={}, productCode={}, benepiaId={}, memcorpCd={}",
                dto.getOrderNo(), dto.getAmount(), dto.getProductCode(), dto.getBenepiaId(), dto.getMemcorpCd());

        KcpPointPayResDto res = service.pointPayAndUpdate(dto);

        return ResponseEntity.ok(ApiResponse.success("포인트 결제 요청 성공", res));
    }

    @PostMapping("/cancel")
    @Operation(summary = "베네피아 KCP 포인트 취소", description = "베네피아 포인트 결제를 취소합니다.")
    public ResponseEntity<ApiResponse<KcpModResDto>> cancel(
            @Valid @RequestBody KcpPointCancelReqDto dto
    ) {

        log.info("[KCP][CANCEL][REQ] orderNo={}, tno={}", dto.getOrderNo(), dto.getTno());

        KcpModResDto res = service.cancelPoint(dto);

        return ResponseEntity.ok(
                ApiResponse.success("포인트 취소 성공", res)
        );
    }

}
