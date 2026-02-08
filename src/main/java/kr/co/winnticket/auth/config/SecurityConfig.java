package kr.co.winnticket.auth.config;

import kr.co.winnticket.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    //베네피아 SSO용
    @Bean
    @Order(0)
    public SecurityFilterChain benepiaChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/benepia**", "/benepia-batch/**")
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
                .securityMatcher("/**")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(form -> form.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth

                        /* ---------- 모든 사용자 접근 허용 (비로그인) ---------- */
                        .requestMatchers(
                                "/api/mair/**",
                                "/api/payletter/**",
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/api/plusn/test/**",
                                "/api/coreworks/test/**",
                                "/api/woongjin/test/**",
                                "/api/spavis/test/**",
                                "/api/playstory/test/**"
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

    // CORS 설절(운영용)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://winnticket.store"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));

        config.setAllowCredentials(true);

        // 운영 도메인 + CloudFront 패턴 허용
        config.setAllowedOrigins(List.of(
                "https://winnticket.store",
                "https://www.winnticket.store",
                "https://d2f2qj7j8l0gx6.cloudfront.net",
                "http://localhost:3000"
        ));

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


