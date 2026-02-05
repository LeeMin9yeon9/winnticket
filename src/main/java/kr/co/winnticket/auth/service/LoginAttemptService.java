package kr.co.winnticket.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginAttemptService { // 로그인 실패 관리

    private final StringRedisTemplate redisTemplate;

    private static final String LOGIN_FAIL_PREFIX = "login:fail:";
    private static final String LOGIN_LOCK_PREFIX = "long:lock:";

    private static final int MAX_FAIL_COUNT = 10; // 로그인 실패 회수
    private static final long LOCK_TIME = 15; // 잠금 시간

    // 로그인 실패 기록 , 현장관리자만
    public void recordFailedAttempt(String accountId, String roleId){
        if(!"ROLE002".equals(roleId)){
            return;
        }
        String key = LOGIN_FAIL_PREFIX + accountId;
        Long fails = redisTemplate.opsForValue().increment(key);

        // 실패 횟수 저장 및 TTL 15분 설정
        if(fails != null && fails == 1){
            redisTemplate.expire(key,LOCK_TIME, TimeUnit.MINUTES);
        }

        if(fails != null && fails >= MAX_FAIL_COUNT){
            String lockKey = LOGIN_LOCK_PREFIX + accountId;
            redisTemplate.opsForValue()
                    .set(lockKey, "LOCK",LOCK_TIME,TimeUnit.MINUTES);
        }
    }

    // 실패 횟수 초기화
    public void resetFailCount(String accountId , String roleId){
        if(!"ROLE002".equals(roleId)){
            return;
        }
        redisTemplate.delete(LOGIN_FAIL_PREFIX + accountId);
    }

    // 현장관리자 계정 잠금 여부만 체크
    public boolean isLocked(String accountId, String roleId){
        if(!"ROLE002".equals(roleId)){
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(LOGIN_LOCK_PREFIX + accountId));
    }
}