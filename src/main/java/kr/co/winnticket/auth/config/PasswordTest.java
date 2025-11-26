package kr.co.winnticket.auth.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// 관리자 패스워드 암호화 (DB 저장용)
public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rwa = "admin123";

        String encoded = encoder.encode(rwa);

        System.out.println("암호화결과 = " + encoded);
        System.out.println("match테스트 = " + encoder.matches("admin123",encoded));
    }
}
