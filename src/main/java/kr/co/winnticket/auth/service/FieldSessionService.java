package kr.co.winnticket.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FieldSessionService {
    private final StringRedisTemplate redisTemplate;

    private String key(String accountId) {
        return "SESSION:" + accountId;
    }

    public void store(String accountId, String sid, long ttlMs) {
        redisTemplate.opsForValue().set(key(accountId), sid, ttlMs, TimeUnit.MILLISECONDS);
    }

    public String get(String accountId) {
        return redisTemplate.opsForValue().get(key(accountId));
    }

    public void delete(String accountId) {
        redisTemplate.delete(key(accountId));
    }

}
