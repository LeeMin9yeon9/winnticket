package kr.co.winnticket.common.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientIpProvider {
    private final HttpServletRequest request;

    /**
     * 클라이언트 실제 IP 추출
     * - 프록시/로드밸런서/Nginx 환경 대응
     * - 우선순위:
     *   1) X-Forwarded-For (첫번째 값)
     *   2) X-Real-IP
     *   3) Proxy-Client-IP
     *   4) WL-Proxy-Client-IP
     *   5) HTTP_CLIENT_IP
     *   6) HTTP_X_FORWARDED_FOR
     *   7) remoteAddr
     */
    public String getClientIp() {
        String ip;

        ip = getFirstIp(request.getHeader("X-Forwarded-For"));
        if (isValid(ip)) return ip;

        ip = getFirstIp(request.getHeader("X-Real-IP"));
        if (isValid(ip)) return ip;

        ip = getFirstIp(request.getHeader("Proxy-Client-IP"));
        if (isValid(ip)) return ip;

        ip = getFirstIp(request.getHeader("WL-Proxy-Client-IP"));
        if (isValid(ip)) return ip;

        ip = getFirstIp(request.getHeader("HTTP_CLIENT_IP"));
        if (isValid(ip)) return ip;

        ip = getFirstIp(request.getHeader("HTTP_X_FORWARDED_FOR"));
        if (isValid(ip)) return ip;

        ip = request.getRemoteAddr();
        return (ip != null) ? ip.trim() : null;
    }

    private String getFirstIp(String value) {
        if (value == null) return null;

        String v = value.trim();
        if (v.isBlank()) return null;

        // ex) "203.0.113.10, 10.0.0.1"
        if (v.contains(",")) {
            v = v.split(",")[0].trim();
        }

        return v;
    }

    private boolean isValid(String ip) {
        if (ip == null) return false;

        String v = ip.trim();
        if (v.isBlank()) return false;

        // 헤더에 unknown 들어오는 케이스 방어
        return !"unknown".equalsIgnoreCase(v);
    }
}
