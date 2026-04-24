package kr.co.winnticket.sms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BizMsgService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * SMS 발송 (비동기)
     * - BIZ_MSG 테이블에 INSERT → 외부 SMS 게이트웨이가 폴링하여 발송
     * - 실패해도 주문/결제에 영향 없음 (fire-and-forget)
     */
    @Async("taskExecutor")
    public void sendSms(String cmid,
                        String destPhone,
                        String destName,
                        String sendPhone,
                        String sendName,
                        String message) {

        try {
            // 중복 방지: cmid가 이미 있으면 스킵
            if (existsCmid(cmid)) return;

            int msgType = decideMsgType(message);

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
        } catch (Exception e) {
            // @Async 메서드의 예외는 호출자에게 전파되지 않음
            // 로그만 남기고 종료 (SMS 실패가 시스템 장애로 이어지지 않도록)
            log.error("[SMS 발송 실패] cmid={}, destPhone={}", cmid, destPhone, e);
        }
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
        if (msg == null) return 0;

        try {
            // 한글을 2바이트로 계산하는 EUC-KR 기준으로 바이트 배열 추출
            int byteLength = msg.getBytes("EUC-KR").length;

            // 90바이트 초과면 MMS(5), 이하면 SMS(0)
            return (byteLength > 90) ? 5 : 0;

        } catch (UnsupportedEncodingException e) {
            // 예외 발생 시 안전하게 글자 수 기반으로 대체 (또는 기본 MMS 처리)
            return (msg.length() > 40) ? 5 : 0;
        }
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }
}
