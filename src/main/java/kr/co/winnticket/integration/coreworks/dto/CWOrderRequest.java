package kr.co.winnticket.integration.coreworks.dto;

import lombok.Data;
import java.util.List;

@Data
public class CWOrderRequest {

    private String channelCd;   // 판매채널코드
    private String orderSeq;    // 주문번호 (우리쪽 주문번호)
    private String buyDate;     // yyyyMMddHHmmss
    private String name;        // 구매자명
    private String hp;          // 휴대폰번호

    private List<Item> itemList;

    @Data
    public static class Item {
        private String pin;       // 티켓 PIN (신규발급 시 임의값 가능)
        private String itemCode;  // 연동 상품코드
        private String itemName;  // 상품명 (선택)
        private String reserveDay; // 예약일 (선택, yyyyMMdd)
    }
}
