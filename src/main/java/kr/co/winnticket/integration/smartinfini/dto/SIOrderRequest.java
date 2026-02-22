package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SIOrderRequest {

    @JsonProperty("order_no")
    private String orderNo; // 주문번호

    @JsonProperty("user_name")
    private String userName; // 구매자명

    @JsonProperty("user_hp")
    private String userHp; // 구매자 핸드폰

    @JsonProperty("user_email")
    private String userEmail; // 구매자 이메일

    @JsonProperty("order_date")
    private String orderDate; // 주문일자

    @JsonProperty("channel_code")
    private String channelCode; // 채널코드

    @JsonProperty("pay_method")
    private String payMethod; // 결제방법

    @JsonProperty("deal_id")
    private String dealId; // 딜번호(사용 x)

    @JsonProperty("class_div")
    private List<ClassDiv> classDiv;


    @Data
    public static class ClassDiv {

        @JsonProperty("ticket_code")
        private String ticketCode; // 티켓번호

        @JsonProperty("goods_code")
        private String goodsCode; // 상품코드

        @JsonProperty("rstart_date")
        private String rstartDate; // 예약시작일(사용x)

        @JsonProperty("rend_date")
        private String rendDate; // 예약종료일(사용 x)

        @JsonProperty("barcode")
        private String barcode; // 채널지정바코드 (사용x)
    }
}
