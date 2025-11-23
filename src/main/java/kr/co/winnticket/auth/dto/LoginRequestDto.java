package kr.co.winnticket.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
// 로그인 요청 DTO
@Getter
@Setter
public class LoginRequestDto {
    @Schema(description = "직원 account / 현장관리자 username")
    private String accountId;

    @Schema(description = "관리자 또는 현장관리자 password")
    private String password;
}
