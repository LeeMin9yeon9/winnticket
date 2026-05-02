package kr.co.winnticket.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * Redis 기반 단순 분산락. 다중 인스턴스 환경에서 같은 스케줄 잡이 동시 실행되는 것을 방지.
 *
 * 사용 예:
 *   String token = lock.acquire("expire-orders", Duration.ofMinutes(5));
 *   if (token == null) return;          // 다른 인스턴스가 점유중 → 스킵
 *   try { ... } finally { lock.release("expire-orders", token); }
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerLock {

    private final StringRedisTemplate redis;

    private static final String PREFIX = "lock:scheduler:";

    /** @return 락 획득 시 토큰, 실패 시 null */
    public String acquire(String name, Duration ttl) {
        String key = PREFIX + name;
        String token = UUID.randomUUID().toString();
        Boolean ok = redis.opsForValue().setIfAbsent(key, token, ttl);
        if (Boolean.TRUE.equals(ok)) {
            return token;
        }
        log.info("[SCHEDULER LOCK] 점유중 — 스킵 name={}", name);
        return null;
    }

    /** 락 해제. 다른 인스턴스가 점유 중이면 해제하지 않음 (token 일치 시에만). */
    public void release(String name, String token) {
        if (token == null) return;
        String key = PREFIX + name;
        try {
            String current = redis.opsForValue().get(key);
            if (token.equals(current)) {
                redis.delete(key);
            }
        } catch (Exception e) {
            log.warn("[SCHEDULER LOCK] release 실패 name={}", name, e);
        }
    }
}
