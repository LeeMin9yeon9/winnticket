package kr.co.winnticket.integration.coreworks.dto;

import lombok.Data;
import java.util.List;

@Data
public class CWOrderResponse {

    private String orderSeq;  // 코어웍스 쪽 주문번호

    private List<Pin> pinList;

    @Data
    public static class Pin {
        private String pin;   // 발급된 티켓 PIN
    }
}
