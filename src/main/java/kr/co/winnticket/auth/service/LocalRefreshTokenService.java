package kr.co.winnticket.auth.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("local")
public class LocalRefreshTokenService extends RefreshTokenService {

    private final ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>();

    public LocalRefreshTokenService() {
        super(null);
    }

    @Override
    public void refreshStore(String accountId, String refreshToken, long ttlMs) {
        if (accountId == null || refreshToken == null || ttlMs <= 0) return;
        tokens.put(accountId, refreshToken);
    }

    @Override
    public String refreshGet(String accountId) {
        if (accountId == null) return null;
        return tokens.get(accountId);
    }

    @Override
    public void refreshDelete(String accountId) {
        if (accountId == null) return;
        tokens.remove(accountId);
    }
}
