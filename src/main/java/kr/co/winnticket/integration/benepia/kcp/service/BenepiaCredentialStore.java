package kr.co.winnticket.integration.benepia.kcp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 혼합결제 시 카드 콜백까지 베네피아 인증 정보를 임시 보관하는 Redis 저장소.
 * TTL 30분. 결제 완료 또는 실패 후 즉시 삭제.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BenepiaCredentialStore {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String ID_PREFIX  = "benepia:pay:id:";
    private static final String PWD_PREFIX = "benepia:pay:pwd:";
    private static final long TTL_MINUTES = 30;

    public void save(UUID orderId, String benepiaId, String benepiaPwd) {
        stringRedisTemplate.opsForValue().set(ID_PREFIX  + orderId, benepiaId,  TTL_MINUTES, TimeUnit.MINUTES);
        stringRedisTemplate.opsForValue().set(PWD_PREFIX + orderId, benepiaPwd, TTL_MINUTES, TimeUnit.MINUTES);
        log.info("[BENEPIA CRED] Redis 저장 orderId={}", orderId);
    }

    /** @return [benepiaId, benepiaPwd] 또는 null (만료/없음) */
    public String[] get(UUID orderId) {
        String id  = stringRedisTemplate.opsForValue().get(ID_PREFIX  + orderId);
        String pwd = stringRedisTemplate.opsForValue().get(PWD_PREFIX + orderId);
        if (id == null || pwd == null) return null;
        return new String[]{id, pwd};
    }

    public void delete(UUID orderId) {
        stringRedisTemplate.delete(ID_PREFIX  + orderId);
        stringRedisTemplate.delete(PWD_PREFIX + orderId);
        log.info("[BENEPIA CRED] Redis 삭제 orderId={}", orderId);
    }
}
