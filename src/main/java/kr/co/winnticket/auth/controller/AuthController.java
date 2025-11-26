package kr.co.winnticket.auth.controller;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.winnticket.auth.config.ApiResDto;
import kr.co.winnticket.auth.dto.LoginRequestDto;
import kr.co.winnticket.auth.dto.LoginResponseDto;
import kr.co.winnticket.auth.dto.LogoutRequestDto;
import kr.co.winnticket.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResDto<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto){
        return ApiResDto.success("로그인 성공",authService.login(loginRequestDto));
    }

    @PostMapping("/logout")
    public ApiResDto<Void> logout(HttpServletRequest request, @RequestBody LogoutRequestDto logoutRequestDto){
        String token = extractToken(request);
        authService.logout(token, logoutRequestDto);

        return ApiResDto.success("로그아웃 성공",null);
    }

    // HTTP 요청 헤더에서 JWT 토큰 뽑아냄
    private String extractToken(HttpServletRequest request){
        String bearer = request.getHeader("Authorization"); // 요청헤더에서 Authorization 값 가져옴
        if(bearer != null && bearer.startsWith("bearer")){ // Bearer 방식인지 체크
            return bearer.substring(7);  //Bearer 이후 실제 JWT 값만 자르기
        }
        return null;
    }
}
