package kr.co.winnticket.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "로그인" , description = "로그인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인 성공" , description = "로그인 성공했습니다.")
    public ApiResDto<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto){
        return ApiResDto.success("로그인 성공",authService.login(loginRequestDto));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 성공" , description = "로그아웃 성공했습니다.")
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
