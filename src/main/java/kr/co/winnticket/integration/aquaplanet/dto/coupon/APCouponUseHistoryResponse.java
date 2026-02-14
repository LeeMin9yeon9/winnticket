package kr.co.winnticket.integration.aquaplanet.dto.coupon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class APCouponUseHistoryResponse {

    @JsonProperty("ds_result")
    private List<Item> dsResult;

    @Data
    public static class Item {
        @JsonProperty("CORP_CD")
        private String corpCd;

        @JsonProperty("CONT_NO")
        private String contNo;

        @JsonProperty("ISSUE_DATE")
        private String issueDate;

        @JsonProperty("REPR_CPON_SEQ")
        private String reprCponSeq;

        @JsonProperty("REPR_CPON_INDICT_NO")
        private String reprCponIndictNo;

        @JsonProperty("GOODS_NO")
        private String goodsNo;

        @JsonProperty("GOODS_NM")
        private String goodsNm;

        @JsonProperty("VALI_PRID_STRT_DATE")
        private String valiPridStrtDate;

        @JsonProperty("VALI_PRID_END_DATE")
        private String valiPridEndDate;

        @JsonProperty("REPR_CPON_STAT_CD")
        private String reprCponStatCd;

        @JsonProperty("REPR_CPON_STAT_NM")
        private String reprCponStatNm;

        @JsonProperty("CLLT_DS")
        private String clltDs;
    }
}
