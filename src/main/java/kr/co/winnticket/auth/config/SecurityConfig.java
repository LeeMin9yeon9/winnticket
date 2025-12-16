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
                                "/api/shop/**",
                                "/api/menu/menuList",
                                "/api/menu/menuList/**",
                                "/api/menu/menuCategory",
                                "/api/menu/menuCategory/**",
                                "/api/product/**",
                                "/api/menu/products/**",
                                "/api/community/event",
                                "/api/community/event/**",
                                "/api/community/faq",
                                "/api/community/faq/**",
                                "/api/community/faq/categories",
                                "/api/shop/popups",
                                "/api/shop/banners",
                                "/api/admin/bank-accounts/visible",
                                "/api/admin/site-info",
                                "/api/admin/site-info/company-intro",
                                "/api/admin/terms/visible"


                        ).permitAll()

                        .requestMatchers(HttpMethod.POST,
                                "/api/orders",
                                "/api/pay/**",
                                "/api/community/qna",
                                "api/community/faq"


                        ).permitAll()

                        .requestMatchers(HttpMethod.PUT,
                                "/api/shop/popups/**",
                                "/api/shop/banners/**"
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

