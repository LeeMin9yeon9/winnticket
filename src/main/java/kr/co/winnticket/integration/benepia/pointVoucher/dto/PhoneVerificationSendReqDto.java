package kr.co.winnticket.integration.benepia.pointVoucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(title = "[포인트 이용권 전환 - 휴대폰 인증번호 발송 요청] PhoneVerificationSendReqDto")
public class PhoneVerificationSendReqDto {

    @NotBlank
    @Schema(description = "휴대폰번호")
    private String phone;

    @Schema(description = "이름 (감사로그용, 이 시점에 알고 있으면 같이 저장)")
    private String customerName;

    @Schema(description = "베네피아 아이디 (감사로그용, 이 시점에 알고 있으면 같이 저장)")
    private String benepiaId;
}
