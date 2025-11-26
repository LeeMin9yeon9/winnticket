package kr.co.winnticket.auth.controller;

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
