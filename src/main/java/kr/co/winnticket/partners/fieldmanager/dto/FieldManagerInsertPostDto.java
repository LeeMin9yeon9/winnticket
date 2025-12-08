package kr.co.winnticket.partners.fieldmanager.dto;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "[파트너 관리 > 현장관리자 생성 요청 DTO] FieldManagerInsertPostDto")
public class FieldManagerInsertPostDto {

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

    @Hidden
    @NotBlank
    @Schema(example = "파트너 아이디")
    private String partnerId;

    @Schema(description = "활성 여부", example = "true")
    private Boolean active = true;

    @Hidden
    @Schema(description = "역할 ID 목록")
    private List<Long> roleIds;



}
