package kr.co.winnticket.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 로그인 응답 data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    @Schema(description = "로그인 성공 여부")
    private boolean success;

    @Schema(description = "로그인 메세지")
    private String message;

    @Schema(description = "로그인 데이터")
    private TokenData data;

    @Getter
    @AllArgsConstructor
    public static class TokenData{
        @Schema(description = "JWT 엑세스 토큰")
        private String accessToken;

        @Schema(description = "JWT 리프레시 토큰")
        private String refreshToken;

        @Schema(description = "로그인 정보 데이터")
        private AuthUserDto user;

    }

    // 로그린 성공
    public static LoginResponseDto success(String accessToken, String refreshToken, AuthUserDto user){
        return new LoginResponseDto(true , "로그인성공" , new TokenData(accessToken,refreshToken,user));
    }

    // 로그인 실패
    public static LoginResponseDto fail(String message){
        return new LoginResponseDto(false,message,null);
    }

}
