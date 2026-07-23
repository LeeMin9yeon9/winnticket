package kr.co.winnticket.integration.benepia.pointVoucher.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kr.co.winnticket.common.dto.ApiResponse;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PhoneVerificationConfirmReqDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PhoneVerificationConfirmResDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PhoneVerificationSendReqDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherExchangeReqDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherExchangeResDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PointVoucherLookupResDto;
import kr.co.winnticket.integration.benepia.pointVoucher.service.PhoneVerificationService;
import kr.co.winnticket.integration.benepia.pointVoucher.service.PointVoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/benepia/voucher")
@Tag(name = "이용권 - 사용자", description = "베네피아 포인트→이용권 전환 및 이용권 조회. 인증 발송/확인, 전환 확정은 베네피아 iframe SSO 세션이 필요하고, 이용권 조회는 이용권 번호만으로 누구나 호출 가능(체크아웃에서 사용)")
public class PointVoucherController {

    private final PhoneVerificationService phoneVerificationService;
    private final PointVoucherService pointVoucherService;

    // 클라이언트가 보낸 값은 무시하고, SSO 진입 시 복호화된 sitecode(회원 소속사코드)로 덮어씀 - 위변조 방지
    private String resolveMemcorpCd(HttpSession session) {
        BenepiaDecryptedParamDto decrypted = (BenepiaDecryptedParamDto) session.getAttribute("BENEP_DECRYPTED");
        if (decrypted == null || !StringUtils.hasText(decrypted.getSitecode())) {
            throw new IllegalStateException("베네피아 웹 진입 후 호출해야 함");
        }
        return decrypted.getSitecode();
    }

    @Operation(summary = "휴대폰 인증번호 발송", description = "인증번호(6자리)를 SMS로 발송합니다. 5분간 유효하며 재발송 시 이전 코드는 즉시 만료 처리됩니다. 인증 불필요.")
    @PostMapping("/phone/verification")
    public ResponseEntity<ApiResponse<Void>> sendPhoneVerification(
            @Valid @RequestBody PhoneVerificationSendReqDto dto) {
        phoneVerificationService.sendCode(dto);
        return ResponseEntity.ok(ApiResponse.success("인증번호가 발송되었습니다.", null));
    }

    @Operation(summary = "휴대폰 인증번호 확인", description = "인증번호를 확인합니다. 성공 시 해당 휴대폰번호는 30분간 전환 확정(exchange) 호출이 가능한 상태로 유지됩니다. 인증 불필요.")
    @PostMapping("/phone/verification/confirm")
    public ResponseEntity<ApiResponse<PhoneVerificationConfirmResDto>> confirmPhoneVerification(
            @Valid @RequestBody PhoneVerificationConfirmReqDto dto) {
        PhoneVerificationConfirmResDto result = phoneVerificationService.confirm(dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "포인트 → 이용권 전환 확정", description = "KCP로 베네피아 포인트를 실제 차감하고 16자리 이용권 번호를 발급합니다. phone은 사전에 인증 완료된 번호여야 하며, 베네피아 iframe SSO 세션 필요(session의 sitecode로 소속사코드를 서버가 강제 설정, 클라이언트 값은 무시). 이용권 발급 DB 저장이 실패하면 이미 차감된 포인트를 KCP로 자동 취소합니다.")
    @PostMapping("/exchange")
    public ResponseEntity<ApiResponse<PointVoucherExchangeResDto>> exchange(
            @Valid @RequestBody PointVoucherExchangeReqDto dto,
            HttpSession session) {
        String memcorpCd = resolveMemcorpCd(session);
        PointVoucherExchangeResDto result = pointVoucherService.exchange(dto, memcorpCd);
        return ResponseEntity.ok(ApiResponse.success("이용권이 발급되었습니다.", result));
    }

    // 결제 시 이용권 조회 - 베네피아 계정/SSO 세션 불필요, 이용권 번호만으로 조회
    @Operation(summary = "이용권 조회 (잔액 확인)", description = "이용권 번호만으로 잔액/상태를 조회합니다. 베네피아 계정이나 SSO 세션이 필요 없어 체크아웃 화면에서 그대로 사용 가능합니다. status가 ACTIVE가 아니거나 validUntil이 지났으면 프론트에서 사용불가로 처리해야 합니다.")
    @GetMapping("/{voucherNumber}")
    public ResponseEntity<ApiResponse<PointVoucherLookupResDto>> lookup(
            @PathVariable String voucherNumber) {
        PointVoucherLookupResDto result = pointVoucherService.lookup(voucherNumber);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
