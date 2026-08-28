package kr.co.winnticket.integration.benepia.pointVoucher.service;

import kr.co.winnticket.common.enums.VerificationPurpose;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PhoneVerificationConfirmReqDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PhoneVerificationConfirmResDto;
import kr.co.winnticket.integration.benepia.pointVoucher.dto.PhoneVerificationSendReqDto;
import kr.co.winnticket.integration.benepia.pointVoucher.mapper.PointVoucherMapper;
import kr.co.winnticket.sms.service.BizMsgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class PhoneVerificationService {

    private static final int CODE_EXPIRE_MINUTES = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PointVoucherMapper mapper;
    private final BizMsgService bizMsgService;

    public void sendCode(PhoneVerificationSendReqDto req) {
        String phone = req.getPhone().replaceAll("[^0-9]", "");
        String purpose = VerificationPurpose.POINT_EXCHANGE.name();
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));

        // 재발송 시 이전에 발급된 미인증 코드는 즉시 만료 처리 (최신 코드만 유효)
        mapper.invalidatePendingVerifications(phone, purpose);

        mapper.insertPhoneVerification(
                UUID.randomUUID(),
                phone,
                code,
                purpose,
                LocalDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES),
                req.getCustomerName(),
                req.getBenepiaId()
        );

        String cmid = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        String message = "[윈앤티켓] 인증번호 [" + code + "]를 입력해주세요.";

        bizMsgService.sendSms(
                cmid,
                phone,
                "",
                "0415455681",
                "윈앤티켓",
                message
        );

        log.info("[PointVoucher] 인증번호 발송 phone={}", phone);
    }

    public PhoneVerificationConfirmResDto confirm(PhoneVerificationConfirmReqDto req) {
        String phone = req.getPhone().replaceAll("[^0-9]", "");
        String purpose = VerificationPurpose.POINT_EXCHANGE.name();

        UUID verificationId = mapper.findValidVerificationId(phone, purpose, req.getCode());
        if (verificationId == null) {
            return new PhoneVerificationConfirmResDto(false);
        }

        mapper.markVerificationVerified(verificationId);
        return new PhoneVerificationConfirmResDto(true);
    }
}
