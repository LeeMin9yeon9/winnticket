package kr.co.winnticket.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.winnticket.auth.config.ApiResDto;
import kr.co.winnticket.auth.dto.*;
import kr.co.winnticket.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "로그인" , description = "로그인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인" , description = "로그인 ")
    public ApiResDto<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto){
        return ApiResDto.success("로그인 성공",authService.login(loginRequestDto));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃" , description = "Access / Refresh Token 블랙리스트 처리")
    public ApiResDto<Void> logout(HttpServletRequest request, @RequestBody LogoutRequestDto logoutRequestDto){
        String token = extractToken(request);
        authService.logout(token, logoutRequestDto);

        return ApiResDto.success("로그아웃 성공",null);
    }

    // Refresh 토큰 재발급
    @PostMapping("/refresh")
    @Operation(summary = "Refresh Token 재발급",
            description = "현장관리자(ROLE002)만 Refresh Token을 사용할 수 있습니다.")
    public ApiResDto<TokenResponseDto> refresh(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto){
        return ApiResDto.success("토큰 재발급 성공",authService.refresh(refreshTokenRequestDto));
    }

    // HTTP 요청 헤더에서 JWT 토큰 뽑아냄
    private String extractToken(HttpServletRequest request){
        String bearer = request.getHeader("Authorization"); // 요청헤더에서 Authorization 값 가져옴
        if(bearer != null && bearer.startsWith("Bearer ")){ // Bearer 방식인지 체크
            return bearer.substring(7);  //Bearer 이후 실제 JWT 값만 자르기
        }
        return null;
    }

    @GetMapping("/test-secure")
    @Operation(summary = "보호된 API 테스트", description = "JWT AccessToken 인증이 필요한 API")
    public ApiResDto<String> secureTest() {
        return ApiResDto.success("AccessToken 인증 성공!", "OK");
    }
}
