package kr.co.winnticket.auth.config;

import kr.co.winnticket.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(csrf -> csrf.disable()) // CSRF 끔
                //.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // 모든요청 허용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                                .anyRequest().permitAll())
//                        .requestMatchers(
//                                "/api/auth/login",
//                                "/api/auth/refresh",
//                                "/api/auth/logout",
//
//                                "/swagger-ui/**",
//                                "/swagger-ui.html",
//                                "/swagger-resources/**",
//                                "/swagger-ui/index.html",
//                                "/v3/api-docs/**",
//
//
//                                "/webjars/**"
//                        ).permitAll() // 인증없이 허용
//                        .anyRequest().authenticated() // 나머지는 API 인증 필요
//                )
                .httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 끔
                .formLogin(form -> form.disable())
                .sessionManagement(session ->session.disable());
       // http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();                            // 현재 모든 인증 끔
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 Origin 등록 == 서버가 허용한 Origin API 호출 가능
        config.addAllowedOrigin("https://api.winnticket.store");
        config.addAllowedOrigin("https://winnticket.store");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",config); // 모든 경로 CORS 정책 허용

        return source;
    }

}
