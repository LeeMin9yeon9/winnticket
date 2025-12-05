package kr.co.winnticket.partners.fieldmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(title = "[파트너 >  현장관리자 PW 초기화 DTO ] ResetPasswordDto")
public class ResetPasswordDto {
    @NotBlank
    @Schema(description = "강제 초기화 PW")
    private String newPassword;
}
