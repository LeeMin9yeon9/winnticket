package kr.co.winnticket.common.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

import java.util.HashSet;
import java.util.Set;

/**
 * 클라이언트가 응답을 받는 도중 연결을 끊어서 발생하는 Broken pipe 계열 예외는
 * 서버 로직 문제가 아니라 상대방이 사라진 정상적인 상황이라 스택트레이스를 남기지 않는다.
 */
public class ClientAbortTurboFilter extends TurboFilter {

    private static final Set<String> IGNORED_EXCEPTION_NAMES = new HashSet<>(Set.of(
            "org.springframework.web.context.request.async.AsyncRequestNotUsableException",
            "org.apache.catalina.connector.ClientAbortException"
    ));

    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (t != null && isClientAbort(t)) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }

    private boolean isClientAbort(Throwable t) {
        Throwable current = t;
        int depth = 0;
        while (current != null && depth < 10) {
            if (IGNORED_EXCEPTION_NAMES.contains(current.getClass().getName())) {
                return true;
            }
            for (Throwable suppressed : current.getSuppressed()) {
                if (IGNORED_EXCEPTION_NAMES.contains(suppressed.getClass().getName())) {
                    return true;
                }
            }
            current = current.getCause();
            depth++;
        }
        return false;
    }
}
