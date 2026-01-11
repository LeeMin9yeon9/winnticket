package kr.co.winnticket.sms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BizMsgService {

    private final JdbcTemplate jdbcTemplate;

    public void sendSms(String cmid,
                        String destPhone,
                        String destName,
                        String sendPhone,
                        String sendName,
                        String message) {

        // (선택) 중복 방지: cmid가 이미 있으면 스킵
        if (existsCmid(cmid)) return;

        int msgType = decideMsgType(message); // 0=SMS, 1=LMS 같은 규칙(환경에 맞게)

        String sql = """
            INSERT INTO BIZ_MSG (
                CMID, MSG_TYPE, STATUS,
                REQUEST_TIME, SEND_TIME,
                DEST_PHONE, DEST_NAME,
                SEND_PHONE, SEND_NAME,
                MSG_BODY
            )
            VALUES (?, ?, 0, NOW(), NOW(), ?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                cmid,
                msgType,
                destPhone,
                safe(destName),
                sendPhone,
                sendName,
                message
        );
    }

    private boolean existsCmid(String cmid) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM BIZ_MSG WHERE CMID = ?",
                Integer.class,
                cmid
        );
        return cnt != null && cnt > 0;
    }

    private int decideMsgType(String msg) {
        // 간단 규칙: 글자 수 기준(환경별로 조정 가능)
        // SMS 90자, LMS 90자 초과
        int len = msg == null ? 0 : msg.length();
        return (len > 90) ? 1 : 0;
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }
}
