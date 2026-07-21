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
@Tag(name = "SHOP 베네피아 포인트-이용권 전환", description = "")
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

    @Operation(summary = "휴대폰 인증번호 발송")
    @PostMapping("/phone/verification")
    public ResponseEntity<ApiResponse<Void>> sendPhoneVerification(
            @Valid @RequestBody PhoneVerificationSendReqDto dto) {
        phoneVerificationService.sendCode(dto);
        return ResponseEntity.ok(ApiResponse.success("인증번호가 발송되었습니다.", null));
    }

    @Operation(summary = "휴대폰 인증번호 확인")
    @PostMapping("/phone/verification/confirm")
    public ResponseEntity<ApiResponse<PhoneVerificationConfirmResDto>> confirmPhoneVerification(
            @Valid @RequestBody PhoneVerificationConfirmReqDto dto) {
        PhoneVerificationConfirmResDto result = phoneVerificationService.confirm(dto);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "포인트 → 이용권 전환 확정")
    @PostMapping("/exchange")
    public ResponseEntity<ApiResponse<PointVoucherExchangeResDto>> exchange(
            @Valid @RequestBody PointVoucherExchangeReqDto dto,
            HttpSession session) {
        String memcorpCd = resolveMemcorpCd(session);
        PointVoucherExchangeResDto result = pointVoucherService.exchange(dto, memcorpCd);
        return ResponseEntity.ok(ApiResponse.success("이용권이 발급되었습니다.", result));
    }

    // 결제 시 이용권 조회 - 베네피아 계정/SSO 세션 불필요, 이용권 번호만으로 조회
    @Operation(summary = "이용권 조회 (잔액 확인)")
    @GetMapping("/{voucherNumber}")
    public ResponseEntity<ApiResponse<PointVoucherLookupResDto>> lookup(
            @PathVariable String voucherNumber) {
        PointVoucherLookupResDto result = pointVoucherService.lookup(voucherNumber);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
