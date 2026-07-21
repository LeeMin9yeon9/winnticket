package kr.co.winnticket.integration.benepia.pointVoucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "[포인트 이용권 전환 - 휴대폰 인증 결과] PhoneVerificationConfirmResDto")
public class PhoneVerificationConfirmResDto {

    @Schema(description = "인증 성공 여부")
    private boolean verified;
}
