package kr.co.winnticket.sms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BizMsgService {

    private final JdbcTemplate jdbcTemplate;

    public void sendPaymentCompletedSms(String destPhone, String customerName, String orderNumber) {

        String message = """
        [윈앤티켓]
        %s님 주문번호 %s
        입금이 확인되어 티켓이 발급되었습니다.
        감사합니다.
        """.formatted(customerName, orderNumber);

        String sql = """
            INSERT INTO BIZ_MSG (
                CMID, MSG_TYPE, STATUS,
                REQUEST_TIME, SEND_TIME,
                DEST_PHONE, DEST_NAME,
                SEND_PHONE, SEND_NAME,
                MSG_BODY
            )
            VALUES (?,5,0,NOW(),NOW(),?,?,?,?,?)
        """;

        String cmid = "ORD-" + orderNumber + "-PAY";

        jdbcTemplate.update(sql,
                cmid,
                destPhone,          // DEST_PHONE
                customerName,       // DEST_NAME
                "025118691",        // SEND_PHONE
                "윈앤티켓",          // SEND_NAME (브랜드명)
                message             // MSG_BODY
        );
    }
}
