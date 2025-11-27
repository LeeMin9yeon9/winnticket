package kr.co.winnticket.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;
    private static final String REFRESH_KEY_PREFIX = "refresh:field:";

    // Refresh Token 저장
    public void refreshStore(String accountId , String refreshToken, long ttlMs){
        if(accountId == null || refreshToken == null || ttlMs <=0) return;

        String key = REFRESH_KEY_PREFIX + accountId;
        redisTemplate.opsForValue().set(key,refreshToken,ttlMs, TimeUnit.MILLISECONDS);
    }

    // Refresh Token 조회
    public String refreshGet(String accountId){
        if(accountId == null) return null;
        return redisTemplate.opsForValue().get(REFRESH_KEY_PREFIX + accountId);
    }

    // Refresh Token 삭제
    public void refreshDelete(String accountId){
        if(accountId == null) return;
        redisTemplate.delete(REFRESH_KEY_PREFIX + accountId);
    }
}
