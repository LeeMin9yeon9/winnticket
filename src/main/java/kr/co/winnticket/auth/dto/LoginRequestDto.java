package kr.co.winnticket.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {
    @Schema(description = "직원 account / 현장관리자 username" , example = "admin")
    private String accountId;

    @Schema(description = "관리자 또는 현장관리자 password", example = "admin123")
    private String password;
}
