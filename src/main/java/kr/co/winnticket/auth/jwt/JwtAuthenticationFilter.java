package kr.co.winnticket.auth.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.winnticket.auth.service.FieldSessionService;
import kr.co.winnticket.auth.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final FieldSessionService fieldSessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        try {
            if (token != null) {
                if (tokenBlacklistService.isBlacklisted(token)) {
                    SecurityContextHolder.clearContext();
                } else {
                    // 토큰을 1회만 파싱하여 Claims 추출
                    Claims claims = jwtTokenProvider.parseClaims(token);

                    if (claims != null) {
                        String accountId = claims.get("accountId", String.class);
                        String roleId = (String) claims.get("roleId");

                        // ROLE002(현장관리자) 동시 로그인 차단
                        if ("ROLE002".equals(roleId)) {
                            String sid = claims.get("sid", String.class);
                            if (sid == null) {
                                SecurityContextHolder.clearContext();
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                return;
                            }

                            String currentSid = fieldSessionService.get(accountId);
                            if (currentSid == null || !currentSid.equals(sid)) {
                                SecurityContextHolder.clearContext();
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("{\"error\":\"SESSION_INVALID\"}");
                                return;
                            }
                        }

                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleId);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        accountId, null, List.of(authority)
                                );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        SecurityContextHolder.clearContext();
                    }
                }
            }
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.debug("JWT filter exception: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

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
                || path.startsWith("/api/woongjin/test")
                || path.startsWith("/api/spavis/test")
                || path.startsWith("/api/playstory/test")
                || path.startsWith("/api/smartinfini")
                || path.startsWith("/api/bankda/test")
                || path.startsWith("/api/bankda")
                || path.startsWith("/api/aquaplanet/test")
                || path.startsWith("/api/payletter")
                || path.startsWith("/api/lscompany");
    }
}
