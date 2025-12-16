package kr.co.winnticket.auth.config;

import kr.co.winnticket.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 끔
                // .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // 모든요청 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/v3/api-docs/**",
                                "/webjars/**"


                        ).permitAll() // 인증없이 허용
                        .requestMatchers(HttpMethod.GET,
                                "/api/shop/**",                      // 판매홈페이지
                                "/api/menu/**",                      // 메뉴
                                "/api/product/shop/**" ,              // 상품
                                "/api/community/**",                 // 커뮤니티
                                "/api/admin/bank-accounts/visible",  // 은행 계좌
                                "/api/admin/site-info/**",           // 회사정보
                                "/api/admin/terms/visible",          // 약관정보
                                "/api/cart/**",                      // 장바구니
                                "/api/shop/banners/**",              //배너
                                "/api/shop/order/**",                 // 주문
                                "/api/common/status"                    // 주문상태


                        ).permitAll()

                        .requestMatchers(HttpMethod.POST,
                                "/api/cart/**",                     // 장바구니 추가하기
                                "/api/orders/**",                   // 주문하기
                                "/api/pay/**",                       // 결제하기
                                "/api/community/qna/**",            // 문의 등록
                                "/api/community/faq/**",            // faq등록
                                "/api/shop/popups/**",            // 팝안 안보이
                                "/api/shop/banners/**"              // 배너 안보이기


                        ).permitAll()

                        //.anyRequest().authenticated() // 나머지는 API 인증 필요

                        // 주문목록 API (관리자 + 현장관리자)
                        .requestMatchers(HttpMethod.GET,
                                "/api/orders/**")
                        .hasAnyAuthority("ROLE001", "ROLE002")

                        // 그 외 모든 API는 관리자만
                        .anyRequest()
                        .hasAuthority("ROLE001")
                )
                .httpBasic(httpBasic -> httpBasic.disable()); // HTTP Basic 끔
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


