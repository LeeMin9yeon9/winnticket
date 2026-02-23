package kr.co.winnticket.integration.plusn.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class PlusNOrderResponse extends PlusNBaseResponse {
    private String order_id;
    private List<Coupon> coupon;

    @Data
    public static class Coupon {
        private String order_sales;
        private String gubun;
        private String coupon_no;
        private String goods_code;
    }
}
