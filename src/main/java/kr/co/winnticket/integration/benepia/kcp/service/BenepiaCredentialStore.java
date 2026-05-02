package kr.co.winnticket.integration.benepia.kcp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 혼합결제 시 카드 콜백까지 베네피아 인증 정보를 임시 보관하는 Redis 저장소.
 * TTL 24시간 (PG 결제창은 보통 30분 내 끝나지만 Redis 만료/장애 안전마진 확보).
 * 결제 완료/실패/취소 후 즉시 삭제.
 *
 * 주의: 영속성 필요 시 추후 DB 암호화 컬럼으로 이전 예정 (현재는 Redis only).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BenepiaCredentialStore {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String ID_PREFIX  = "benepia:pay:id:";
    private static final String PWD_PREFIX = "benepia:pay:pwd:";
    private static final long TTL_HOURS = 24;

    public void save(UUID orderId, String benepiaId, String benepiaPwd) {
        try {
            stringRedisTemplate.opsForValue().set(ID_PREFIX  + orderId, benepiaId,  TTL_HOURS, TimeUnit.HOURS);
            stringRedisTemplate.opsForValue().set(PWD_PREFIX + orderId, benepiaPwd, TTL_HOURS, TimeUnit.HOURS);
            log.info("[BENEPIA CRED] Redis 저장 orderId={} ttl={}h", orderId, TTL_HOURS);
        } catch (Exception e) {
            log.error("[BENEPIA CRED] Redis 저장 실패 orderId={} - 결제 진행 시 콜백 처리에 영향 가능", orderId, e);
            throw e; // 저장 실패는 호출자가 인지해야 함 (결제 흐름 중단)
        }
    }

    /** @return [benepiaId, benepiaPwd] 또는 null (만료/없음/Redis 장애) */
    public String[] get(UUID orderId) {
        try {
            String id  = stringRedisTemplate.opsForValue().get(ID_PREFIX  + orderId);
            String pwd = stringRedisTemplate.opsForValue().get(PWD_PREFIX + orderId);
            if (id == null || pwd == null) {
                log.warn("[BENEPIA CRED] Redis 조회 결과 없음 orderId={} idPresent={} pwdPresent={}",
                        orderId, id != null, pwd != null);
                return null;
            }
            return new String[]{id, pwd};
        } catch (Exception e) {
            log.error("[BENEPIA CRED] Redis 조회 실패 orderId={}", orderId, e);
            return null;
        }
    }

    public void delete(UUID orderId) {
        try {
            stringRedisTemplate.delete(ID_PREFIX  + orderId);
            stringRedisTemplate.delete(PWD_PREFIX + orderId);
            log.info("[BENEPIA CRED] Redis 삭제 orderId={}", orderId);
        } catch (Exception e) {
            log.error("[BENEPIA CRED] Redis 삭제 실패 orderId={}", orderId, e);
        }
    }
}
