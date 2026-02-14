package kr.co.winnticket.integration.aquaplanet.dto.coupon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class APCouponIssueRequest {

    @JsonProperty("ds_input")
    private List<Input> dsInput;

    @Data
    @Builder
    public static class Input {
        @JsonProperty("CORP_CD")
        private String corpCd;

        @JsonProperty("CONT_NO")
        private String contNo;

        @JsonProperty("SEQ")
        private Integer seq;

        @JsonProperty("ISSUE_DATE")
        private String issueDate; // YYYYMMDD

        @JsonProperty("GOODS_NO")
        private String goodsNo;

        @JsonProperty("ISSUE_QTY")
        private Integer issueQty;

        @JsonProperty("UNITY_ISSUE_YN")
        private String unityIssueYn; // 문서: N 단장발권 / Y 유통발권

        @JsonProperty("RCVER_NM")
        private String rcverNm;

        @JsonProperty("RCVER_TEL_NATION_NO")
        private String rcverTelNationNo;

        @JsonProperty("RCVER_TEL_AREA_NO")
        private String rcverTelAreaNo;

        @JsonProperty("RCVER_TEL_EXCHGE_NO")
        private String rcverTelExchgeNo;

        @JsonProperty("RCVER_TEL_NO")
        private String rcverTelNo;
    }
}
