package kr.co.winnticket.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidMs;
    private final long refreshTokenValidMs;
    private final JwtParser jwtParser; // 파서 캐싱

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity}") long accessTokenValidMs,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidMs
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidMs = accessTokenValidMs;
        this.refreshTokenValidMs = refreshTokenValidMs;
        // JwtParser를 한 번만 생성하여 재사용
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
        log.info("JWT Token Provider initialized");
    }

    // Access Token 생성
    public String createAccessToken(String id, String name, String accountId, String roleId, String userType, String partnerId, String sid) {
        long now = System.currentTimeMillis();

        Claims claims = Jwts.claims().setSubject(id);
        claims.put("name", name);
        claims.put("accountId", accountId);
        claims.put("roleId", roleId);
        claims.put("userType", userType);
        claims.put("sid", sid);
        if (partnerId != null) claims.put("partnerId", partnerId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessTokenValidMs))
                .signWith(secretKey)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String id, String accountId, String roleId, String sid) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(id)
                .claim("accountId", accountId)
                .claim("roleId", roleId)
                .claim("type", "refresh")
                .claim("sid", sid)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTokenValidMs))
                .signWith(secretKey)
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validate(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰 파싱 (1회 파싱으로 유효성 검증 + Claims 추출)
    // 유효하면 Claims 반환, 무효하면 null 반환
    public Claims parseClaims(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    // Claims 추출
    public Claims getClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    // 토큰 만료시간 timestamp(ms)로 반환
    public long getExpiration(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration().getTime();
    }

    // 만료된 토큰도 Claims 추출 (logout용)
    public Claims getClaimsAllowExpired(String token) {
        try {
            return getClaims(token);
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
