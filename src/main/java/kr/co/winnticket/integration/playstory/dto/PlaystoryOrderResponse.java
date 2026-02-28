package kr.co.winnticket.integration.playstory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PlaystoryOrderResponse {

    @JsonProperty("OPT_LIST")
    private List<OptOrdResult> optList;

    @Data
    public static class OptOrdResult {

        @JsonProperty("result_code")
        private String resultCode;

        @JsonProperty("CPN_NO")
        private String cpnNo;

        @JsonProperty("code")
        private String code;

        @JsonProperty("PRI_PROD")
        private String priProd;

        @JsonProperty("end_date")
        private String endDate;

        @JsonProperty("package_no")
        private String packageNo;

        @JsonProperty("option_price_id")
        private String optionPriceId;

        @JsonProperty("option_price")
        private String optionPrice;

        @JsonProperty("OPT_IDX")
        private String optIdx;

        @JsonProperty("option_name")
        private String optionName;

        @JsonProperty("product_code")
        private String productCode;

        @JsonProperty("option_code")
        private String optionCode;

        @JsonProperty("order_name")
        private String orderName;

        @JsonProperty("bind_no")
        private String bindNo;

        @JsonProperty("barcode_url")
        private String barcodeUrl;

        @JsonProperty("shop_order_no")
        private String shopOrderNo;

        @JsonProperty("ticket_no")
        private String ticketNo;

        @JsonProperty("ticket_type_name")
        private String ticketTypeName;

        @JsonProperty("user_opt")
        private String userOpt;

        @JsonProperty("start_date")
        private String startDate;

        @JsonProperty("result_message")
        private String resultMessage;
    }
}
