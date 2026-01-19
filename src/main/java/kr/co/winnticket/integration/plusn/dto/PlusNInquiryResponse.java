package kr.co.winnticket.integration.plusn.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlusNInquiryResponse extends PlusNBaseResponse {
    private String order_id;
    private List<Coupon> coupon;

    @Data
    public static class Coupon {
        private String order_sales;
        private String gubun;
        private String coupon_no;
        private String goods_code;
        private String status;   // 사용/미사용 여부 (문서에 있음)
    }
}
