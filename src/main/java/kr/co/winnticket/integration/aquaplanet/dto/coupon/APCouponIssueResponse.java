package kr.co.winnticket.integration.aquaplanet.dto.coupon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class APCouponIssueResponse {

    @JsonProperty("ds_output")
    private List<Output> dsOutput;

    @Data
    public static class Output {
        @JsonProperty("CORP_CD")
        private String corpCd;

        @JsonProperty("REPR_CPON_SEQ")
        private String reprCponSeq;

        @JsonProperty("REPR_CPON_NO")
        private String reprCponNo;

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

        @JsonProperty("GOODS_AMT")
        private String goodsAmt;
    }
}
