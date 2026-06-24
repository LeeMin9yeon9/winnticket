package kr.co.winnticket.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import kr.co.winnticket.auth.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("local")
public class LocalTokenBlacklistService extends TokenBlacklistService {

    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    public LocalTokenBlacklistService(JwtTokenProvider jwtTokenProvider) {
        super(null, jwtTokenProvider);
    }

    @Override
    public void blacklistAccessToken(String token) {
        if (token == null) return;
        long ttlMs = getRemainingTtlMs(token);
        if (ttlMs > 0) {
            blacklist.put("access:" + token, System.currentTimeMillis() + ttlMs);
        }
    }

    @Override
    public void blacklistRefreshToken(String token) {
        if (token == null) return;
        long ttlMs = getRemainingTtlMs(token);
        if (ttlMs > 0) {
            blacklist.put("refresh:" + token, System.currentTimeMillis() + ttlMs);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        if (token == null) return false;
        long now = System.currentTimeMillis();
        Long a = blacklist.get("access:" + token);
        if (a != null && a > now) return true;
        Long r = blacklist.get("refresh:" + token);
        return r != null && r > now;
    }
}
