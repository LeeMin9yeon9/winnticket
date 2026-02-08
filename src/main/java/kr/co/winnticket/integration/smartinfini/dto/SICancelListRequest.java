package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SICancelListRequest {

    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("result_date")
    private String resultDate; // yyyy-MM-dd HH:mm:ss 또는 yyyyMMddHHmmss 등 문서 기준

    @JsonProperty("cancelList")
    private List<CancelItem> cancelList;

    @Data
    public static class CancelItem {
        @JsonProperty("order_sales")
        private String orderSales;
    }
}
