package kr.co.winnticket.integration.benepia.pointVoucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(title = "[포인트 이용권 전환 - 휴대폰 인증번호 확인 요청] PhoneVerificationConfirmReqDto")
public class PhoneVerificationConfirmReqDto {

    @NotBlank
    @Schema(description = "휴대폰번호")
    private String phone;

    @NotBlank
    @Schema(description = "인증번호")
    private String code;
}
