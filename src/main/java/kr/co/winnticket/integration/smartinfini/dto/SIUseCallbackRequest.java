package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SIUseCallbackRequest {

    @JsonProperty("order_div")
    private String orderDiv; // 구분 51: 사용, 50 : 사용처리 취소

    @JsonProperty("ticket_code")
    private String ticketCode; // 주문시 사용한 ticket_code

    @JsonProperty("order_sales")
    private String orderSales; // 스마트인피니 주문번호

    @JsonProperty("result_date")
    private String resultDate; // 사용일시

    @JsonProperty("coupon_no")
    private String couponNo; // 쿠폰번호

}