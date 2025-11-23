//package kr.co.winnticket.auth.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http.csrf(csrf -> csrf.disable());
//        http.sessionManagement(session ->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        );
//
//        // 기본 로그인 화면 제거
//        http.formLogin(form -> form.disable());
//        http.httpBasic(basic -> basic.disable());
//
//        // API 권한 규칙 설정
//        http.authorizeHttpRequests(auth -> auth
//                .requestMatchers("/auth/login").permitAll()  //어떤 URL에 규칙 적용할지 // 로그인 API 허용 // 누구나 접근 가능함  == 로그인API에 누구나 접근 가능
//             //   .requestMatchers(("/partner/**").hasRole("PARTNET") // 파트너 전용
//                .anyRequest().authenticated()                // 로그인한 사용자만 접속
//        );
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
//
