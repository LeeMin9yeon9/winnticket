package kr.co.winnticket.integration.woongjin.dto;

import lombok.Data;

@Data
public class WJResendRequest {

    // 주문 pk (알고 있으면 이걸 사용)
    private Integer reservation_id;

    // 또는 채널 주문번호 (일반적으로 이걸 사용)
    private String channel_order_number;
}
