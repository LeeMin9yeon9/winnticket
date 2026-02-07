package kr.co.winnticket.integration.playstory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PlaystoryOrderRequest {

    @JsonProperty("CHN_ID")
    private String chnId;   // 판매채널 ID (P240076)

    @JsonProperty("ORD_PHONE")
    private String ordPhone;

    @JsonProperty("ORD_NAME")
    private String ordName;

    @JsonProperty("ORD_EMAIL")
    private String ordEmail;

    @JsonProperty("REC_PHONE")
    private String recPhone;

    @JsonProperty("REC_NAME")
    private String recName;

    @JsonProperty("REC_EMAIL")
    private String recEmail;

    @JsonProperty("SHOP_ORDER_NO")
    private String shopOrderNo;

    @JsonProperty("OPT_LIST")
    private List<OptOrd> optList;

    @Data
    public static class OptOrd {

        @JsonProperty("OPT_CD")
        private String optCd;

        @JsonProperty("OPT_AMOUNT")
        private Integer optAmount;

        @JsonProperty("PROD_CD")
        private String prodCd;

        @JsonProperty("OPT_PRICE")
        private Integer optPrice;

        @JsonProperty("CPN_NO")
        private String cpnNo;
    }
}
