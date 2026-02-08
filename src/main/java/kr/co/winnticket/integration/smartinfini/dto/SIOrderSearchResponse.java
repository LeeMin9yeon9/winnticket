package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class SIOrderSearchResponse {

    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("inquiryList")
    private List<InquiryItem> inquiryList;

    @Data
    public static class InquiryItem {
        @JsonProperty("return_div")
        private String returnDiv;

        @JsonProperty("return_msg")
        private String returnMsg;

        @JsonProperty("order_sales")
        private String orderSales;

        @JsonProperty("result_date")
        private String resultDate;
    }
}