package kr.co.winnticket.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonAuthEntryPoints {

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static AuthenticationEntryPoint authenticationEntryPoint() {
        return (HttpServletRequest req, HttpServletResponse res, AuthenticationException ex) ->
                writeJson(res, HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다.");
    }

    public static AccessDeniedHandler accessDeniedHandler() {
        return (HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) ->
                writeJson(res, HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
    }

    private static void writeJson(HttpServletResponse res, int status, String message) throws IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("data", null);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString());

        res.getWriter().write(MAPPER.writeValueAsString(body));
    }
}
