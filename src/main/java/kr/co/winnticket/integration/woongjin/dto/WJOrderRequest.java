package kr.co.winnticket.integration.woongjin.dto;

import lombok.Data;
import java.util.List;

@Data
public class WJOrderRequest {

    // 웅진이 요구하는 주문번호 (우리쪽 주문번호)
    private String channel_order_number;

    // 구매자 정보
    private String user_name;
    private String user_phone;
    private String user_email;

    // 상품 옵션 리스트
    private List<Option> options;

    @Data
    public static class Option {
        private String product_channel_order_number;   // 채널 상품 주문번호(티켓번호 활용 -> 웅진은 핀번호를 생성해줌 이 핀번호가 진짜 티켓번호)
        private Integer option_id;    // 옵션 pk
    }
}
