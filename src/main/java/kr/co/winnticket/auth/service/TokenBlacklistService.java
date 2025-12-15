package kr.co.winnticket.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import kr.co.winnticket.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String ACCESS_BLACKLIST_PREFIX = "blacklist:access:";
    private static final String REFRESH_BLACKLIST_PREFIX = "blacklist:refresh:";

    //Access Token 블랙리스트 등록
    public void blacklistAccessToken(String token){
        if(token == null) return;
        long ttlMs = getRemainingTtlMs(token);
        if(ttlMs > 0){
            String key = ACCESS_BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key,"1",ttlMs, TimeUnit.MILLISECONDS);
        }
    }

    // Refresh Token 블랙리스트 등록
    public void blacklistRefreshToken(String token){
        if(token == null) return;

        long ttlMs = getRemainingTtlMs(token);
        if(ttlMs > 0){
            String key = REFRESH_BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key,"1",ttlMs,TimeUnit.MILLISECONDS);
        }
    }

    // 블랙리스트 여부 확인
    public boolean isBlacklisted(String token){

        if(token == null) return false;

        String accessKey = ACCESS_BLACKLIST_PREFIX + token;
        String refreshKey = REFRESH_BLACKLIST_PREFIX + token;

        return Boolean.TRUE.equals(redisTemplate.hasKey(accessKey))
                || Boolean.TRUE.equals(redisTemplate.hasKey(refreshKey));
    }

    // jwt 남은 TTL 계산
    public long getRemainingTtlMs(String token) {
        try {
            long expiration = jwtTokenProvider.getExpiration(token);
            long now = System.currentTimeMillis();
            return expiration - now;
        } catch (ExpiredJwtException e) {

            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

}
