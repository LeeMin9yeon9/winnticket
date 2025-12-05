package kr.co.winnticket.partners.fieldmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(title = "[파트너 >  현장관리자 수정 DTO ] UpdateFieldManagerDto")
public class UpdateFieldManagerDto {
    @NotBlank
    @Schema(description = "로그인 아이디")
    private String userName;

    @NotBlank
    @Schema(description = "비밀번호")
    private String password;

    @NotBlank
    @Schema(example = "현장관리자 이름")
    private String name;

    @Schema(example = "현장관리자 이메일")
    private String email;

    @Schema(example = "현장관리자 핸드폰번호")
    private String phone;

    @Schema(description = "활성여부")
    private boolean active;
}
