package kr.co.winnticket.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
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
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
        this.accessTokenValidMs = accessTokenValidMs;
        this.refreshTokenValidMs = refreshTokenValidMs;
    }

    public String createAccessToken(String userId, String name, String accountId, String roleId, String userType, String partnerId){
        long now = System.currentTimeMillis();

        Claims claims = Jwts.claims().setSubject(userId);
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

    public String createRefreshToken(String userId){
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(userId)
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
}


