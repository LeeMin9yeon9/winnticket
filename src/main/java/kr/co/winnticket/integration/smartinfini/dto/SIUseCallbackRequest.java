package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SIUseCallbackRequest {

    // 문서 예시(POST JSON): order_div, ticket_code, order_sales, result_date, coupon_no
    @JsonProperty("order_div")
    private String orderDiv;

    @JsonProperty("ticket_code")
    private String ticketCode;

    @JsonProperty("order_sales")
    private String orderSales;

    @JsonProperty("result_date")
    private String resultDate;

    @JsonProperty("coupon_no")
    private String couponNo;

    @JsonProperty("order_no")
    private String orderNo;
}