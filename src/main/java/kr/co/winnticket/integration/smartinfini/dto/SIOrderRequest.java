package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SIOrderRequest {

    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_hp")
    private String userHp;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("order_date")
    private String orderDate;

    @JsonProperty("channel_code")
    private String channelCode;

    @JsonProperty("pay_method")
    private String payMethod;

    @JsonProperty("deal_id")
    private String dealId;

    @JsonProperty("class_div")
    private List<ClassDiv> classDiv;


    @Data
    public static class ClassDiv {

        @JsonProperty("ticket_code")
        private String ticketCode;

        @JsonProperty("goods_code")
        private String goodsCode;

        @JsonProperty("rstart_date")
        private String rstartDate;

        @JsonProperty("rend_date")
        private String rendDate;

        @JsonProperty("barcode")
        private String barcode;
    }
}
