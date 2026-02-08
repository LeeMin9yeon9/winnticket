package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SIProductResponse {
        @JsonProperty("goods_default_no")
        private Integer goodsDefaultNo;

        @JsonProperty("goods_default_name")
        private String goodsDefaultName;

        @JsonProperty("price_1")
        private Integer price1;

        @JsonProperty("normal_1")
        private Integer normal1;

        @JsonProperty("valid_date1")
        private String validDate1;

        @JsonProperty("valid_date2")
        private String validDate2;

        @JsonProperty("send_msg")
        private String sendMsg;

        @JsonProperty("merg_msg")
        private String mergMsg;

        @JsonProperty("goods_default_flag")
        private String goodsDefaultFlag;

        @JsonProperty("facilities_interface")
        private String facilitiesInterface;

        @JsonProperty("package_flag")
        private String packageFlag;

        @JsonProperty("package_insert")
        private String packageInsert;

        @JsonProperty("channel_url ")
        private String channelUrl;
}