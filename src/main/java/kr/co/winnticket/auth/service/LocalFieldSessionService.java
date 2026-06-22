package kr.co.winnticket.auth.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("local")
public class LocalFieldSessionService extends FieldSessionService {

    private final ConcurrentHashMap<String, String> sessions = new ConcurrentHashMap<>();

    public LocalFieldSessionService() {
        super(null);
    }

    @Override
    public void store(String accountId, String sid, long ttlMs) {
        sessions.put(accountId, sid);
    }

    @Override
    public String get(String accountId) {
        return sessions.get(accountId);
    }

    @Override
    public void delete(String accountId) {
        sessions.remove(accountId);
    }
}
