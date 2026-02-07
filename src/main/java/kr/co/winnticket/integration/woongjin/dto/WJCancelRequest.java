package kr.co.winnticket.integration.woongjin.dto;

import lombok.Data;
import java.util.List;

@Data
public class WJCancelRequest {

    // 채널 주문번호
    private String channel_order_number;

    // 취소할 상품 목록
    private List<CancelProduct> products;

    @Data
    public static class CancelProduct {
        // 채널 상품 주문번호
        private String product_channel_order_number;
    }
}