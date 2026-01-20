package kr.co.winnticket.integration.coreworks.dto;

import lombok.Data;

@Data
public class CWMmsResendRequest {
    private String channelCd;
    private String orderSeq;
    private String hp; // 없으면 주문등록 시 번호로 발송 :contentReference[oaicite:15]{index=15}
}
