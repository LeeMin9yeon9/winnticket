package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class SICancelListResponse {

    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("cancelList")
    private List<CancelResult> cancelList;

    @Data
    public static class CancelResult {
        @JsonProperty("order_sales")
        private String orderSales;

        @JsonProperty("return_div")
        private String returnDiv;

        @JsonProperty("return_msg")
        private String returnMsg;
    }
}
