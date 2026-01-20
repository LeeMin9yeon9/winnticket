package kr.co.winnticket.integration.coreworks.dto;

import lombok.Data;

import java.util.List;

@Data
public class CWCancelResponse {
    private String orderSeq;
    private List<Pin> pinList;

    @Data
    public static class Pin {
        private String pin;       // 티켓 PIN (신규발급 시 임의값 가능)
        private String code;
    }
}
