package kr.co.winnticket.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

// 로그인 응답 data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LoginResponseDto {
    @Schema(description = "JWT 엑세스 토큰")
    private String accessToken;

    @Schema(description = "JWT 리프레시 토큰")
    private String refreshToken;

    @Schema(description = "로그인 정보 데이터")
    private AuthUserDto user;


}
