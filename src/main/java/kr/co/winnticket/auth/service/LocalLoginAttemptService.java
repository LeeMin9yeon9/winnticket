package kr.co.winnticket.auth.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("local")
public class LocalLoginAttemptService extends LoginAttemptService {

    private final ConcurrentHashMap<String, Integer> failCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> locks = new ConcurrentHashMap<>();

    public LocalLoginAttemptService() {
        super(null);
    }

    @Override
    public void recordFailedAttempt(String accountId, String roleId) {
        if (!"ROLE002".equals(roleId)) return;
        int count = failCounts.merge(accountId, 1, Integer::sum);
        if (count >= 10) {
            locks.put(accountId, System.currentTimeMillis() + 15 * 60 * 1000);
        }
    }

    @Override
    public void resetFailCount(String accountId, String roleId) {
        if (!"ROLE002".equals(roleId)) return;
        failCounts.remove(accountId);
    }

    @Override
    public boolean isLocked(String accountId, String roleId) {
        if (!"ROLE002".equals(roleId)) return false;
        Long lockUntil = locks.get(accountId);
        if (lockUntil == null) return false;
        if (System.currentTimeMillis() > lockUntil) {
            locks.remove(accountId);
            return false;
        }
        return true;
    }
}
