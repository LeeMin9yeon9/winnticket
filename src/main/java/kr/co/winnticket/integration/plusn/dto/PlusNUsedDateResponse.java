package kr.co.winnticket.integration.plusn.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlusNUsedDateResponse extends PlusNBaseResponse {
    private String order_date;
    private List<Coupon> coupon;

    @Data
    public static class Coupon {
        private String order_sales;
        private String coupon_no;
        private String goods_code;
        private String used_time; // 사용시각
    }
}
