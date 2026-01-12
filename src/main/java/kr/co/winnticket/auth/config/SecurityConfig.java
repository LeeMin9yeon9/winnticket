package kr.co.winnticket.auth.config;

import kr.co.winnticket.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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

    //베네피아 SSO용
    @Bean
    @Order(0)
    public SecurityFilterChain benepiaChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/benepia/**",
                        "/benepia")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        return http.build();
    }

    @Bean
    @Order(1)
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )
                .authorizeHttpRequests(auth -> auth


                        /* ---------- 모든 사용자 접근 허용 (비로그인) ---------- */
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/shop/**",
                                "/api/product/shop/**",
                                "/api/community/**",
                                "/api/admin/bank-accounts/visible",
                                "/api/admin/site-info/**",
                                "/api/admin/terms/visible",
                                "/api/cart/**",
                                "/api/shop/banners/**",
                                "/api/orders/shop/**",
                                "/api/common/status",
                                "/api/shopCart/**",
                                "/api/admin/menu/menuCategory/shopMenus/**",
                                "/api/channels/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST,
                                "/api/cart/**",
                                "/api/orders/**",
                                "/api/pay/**",
                                "/api/community/qna/**",
                                "/api/community/faq/**",
                                "/api/shop/popups/**",
                                "/api/shop/banners/**",
                                "/api/shopCart/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.PATCH,
                                "/api/shopCart/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/shopCart/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.PUT,
                                "/api/shopCart/**"
                        ).permitAll()

                        /* ---------- 주문 목록 (ROLE001 + ROLE002 가능) ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/admin/order/tickets/**")
                        .hasAnyAuthority("ROLE001", "ROLE002")
                        .requestMatchers(HttpMethod.POST, "/api/admin/order/tickets/**")
                        .hasAnyAuthority("ROLE001", "ROLE002")


                        /* ---------- 관리자 API (ROLE001만 허용) ---------- */
                        .requestMatchers(HttpMethod.GET, "/api/admin/**")
                        .hasAuthority("ROLE001")

                        .requestMatchers(HttpMethod.POST, "/api/admin/**")
                        .hasAuthority("ROLE001")

                        .requestMatchers(HttpMethod.PUT, "/api/admin/**")
                        .hasAuthority("ROLE001")

                        .requestMatchers(HttpMethod.PATCH, "/api/admin/**")
                        .hasAuthority("ROLE001")

                        .requestMatchers(HttpMethod.DELETE, "/api/admin/**")
                        .hasAuthority("ROLE001")

                        /* ---------- 나머지는 인증 필요 ---------- */
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic.disable());

        // JWT 필터 등록
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
