package kr.co.winnticket.integration.coreworks.dto;

import lombok.Data;

import java.util.List;

@Data
public class CWSearchResponse {

    private String orderSeq;    // 주문번호
    private List<Pin> pinList;

    @Data
    public static class Pin {
        private String pin;         // 티켓 PIN
        private String code;    // 상품코드
        private String buyDate;       // 구매일시
        private String useDate;     // 사용일시
        private String cancelDate;     // 취소일자
    }
}
