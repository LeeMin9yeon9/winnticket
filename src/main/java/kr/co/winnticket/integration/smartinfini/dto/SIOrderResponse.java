package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SIOrderResponse extends SIBaseResponse {

    @JsonProperty("order_no")
    private String orderNo;

    // 문서 예시: gcoupon: null
    @JsonProperty("gcoupon")
    private Object gcoupon;

    @JsonProperty("coupon")
    private List<Coupon> coupon;

    @Getter
    @Setter
    public static class Coupon {
        @JsonProperty("ticket_code")
        private String ticketCode;

        @JsonProperty("order_sales")
        private String orderSales;

        @JsonProperty("coupon_no")
        private String couponNo;
    }
}