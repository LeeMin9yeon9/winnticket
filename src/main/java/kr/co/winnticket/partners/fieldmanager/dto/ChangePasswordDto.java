package kr.co.winnticket.partners.fieldmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(title = "[파트너 >  현장관리자 PW 변경 DTO ] ChangePasswordDto")
public class ChangePasswordDto {

    @NotBlank
    @Schema(description = "현재 비밀번호")
    private String currentPassword;

    @NotBlank
    @Schema(description = "변경 후 비밀번호")
    private String newPassword;


}
