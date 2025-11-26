package kr.co.winnticket.auth.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.winnticket.auth.config.ApiResDto;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Base64;
@Profile("local")
@RestController
@RequestMapping("/api/auth")
@Tag(name = "JWT Secret Key 랜덤 생성 API ", description = "로컬 개발환경에서 Base64 URL-safe 문자열을 생성합니다.")
public class SecretGeneratorController {

    @GetMapping("/generate-secret")
    public ApiResDto<String> generateSecret(){
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[64];
        random.nextBytes(keyBytes);

        String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(keyBytes);

        return ApiResDto.success("JWT Secret 생성 성공 ",secret);
    }


}
