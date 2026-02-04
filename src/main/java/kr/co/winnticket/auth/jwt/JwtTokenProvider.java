package kr.co.winnticket.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenValidMs;
    private final long refreshTokenValidMs;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity}") long accessTokenValidMs,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidMs
    ) {
         System.out.println("=== SERVER SECRET ===");
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
       // this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
        this.accessTokenValidMs = accessTokenValidMs;
        this.refreshTokenValidMs = refreshTokenValidMs;
    }

    // Access Token 생성
    public String createAccessToken(String id, String name, String accountId, String roleId, String userType, String partnerId){
        long now = System.currentTimeMillis();

        Claims claims = Jwts.claims().setSubject(id);
        claims.put("name", name);
        claims.put("accountId", accountId);
        claims.put("roleId", roleId);
        claims.put("userType", userType);
        if (partnerId != null) claims.put("partnerId", partnerId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessTokenValidMs))
                .signWith(secretKey)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String id, String accountId, String roleId){
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(id)
                .claim("accountId",accountId)
                .claim("roleId",roleId)
                .claim("type", "refresh")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTokenValidMs))
                .signWith(secretKey)
                .compact();
    }

    // 토큰 유효성 검증
    public boolean validate(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    // Claims 추출
    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 토큰 만료시간 timestamp(ms)로 반환
    public long getExpiration(String token){
        Claims claims = getClaims(token);
        return claims.getExpiration().getTime();
    }

    // 만료된 토큰도 Claims 추출 (logout용)
    public Claims getClaimsAllowExpired(String token) {
        try {
            return getClaims(token);
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // ⭐ 이게 핵심
        }
    }
}


