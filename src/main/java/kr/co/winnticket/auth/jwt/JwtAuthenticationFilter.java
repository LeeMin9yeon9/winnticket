package kr.co.winnticket.auth.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.winnticket.auth.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String token = resolveToken(request);

        System.out.println("=== JWT FILTER START ===");
        System.out.println("REQUEST URI = " + path);
        System.out.println("AUTHORIZATION HEADER = " + request.getHeader("Authorization"));

        try {
            // 토큰 존재 시만 처리
            if (token != null) {

                // 블랙리스트 확인 (로그아웃된 토큰)
                if (tokenBlacklistService.isBlacklisted(token)) {
                    System.out.println("Blacklisted Token");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                // 토큰 유효성 검증
                if (!jwtTokenProvider.validate(token)) {
                    System.out.println("Invalid Token");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                // Claims 파싱
                Claims claims = jwtTokenProvider.getClaims(token);
                String accountId = claims.getSubject();
                String roleId = (String) claims.get("roleId");

                // 권한 생성
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleId);

                // 인증 정보 생성 후 SecurityContext 에 저장
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                accountId, null, List.of(authority)
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("AUTH SUCCESS | USER = " + accountId + ", ROLE = " + roleId);
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            System.out.println("JWT Filter Exception: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    /**
     * Authorization 헤더에서 Bearer Token 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");

        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        return null;
    }

    /**
     * 필터를 적용하지 않을 요청
     * 로그인 / 토큰 재발급 / 로그아웃은 인증필터를 거치지 않음
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();


        return path.startsWith("/api/auth/login")
                || path.startsWith("/api/auth/refresh")
                || path.startsWith("/api/auth/logout")
                || path.equals("/benepia")
                || path.startsWith("/benepia/")
                || path.startsWith("/api/plusn/test")
                || path.startsWith("/api/coreworks/test")
                || path.startsWith("/api/woongjin/test");
    }
}
