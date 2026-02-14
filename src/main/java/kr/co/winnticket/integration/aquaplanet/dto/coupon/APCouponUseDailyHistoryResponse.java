package kr.co.winnticket.integration.aquaplanet.dto.coupon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class APCouponUseDailyHistoryResponse {

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

        @JsonProperty("BSN_DATE")
        private String bsnDate;

        @JsonProperty("CLLT_BRCH_CD")
        private String clltBrchCd;

        @JsonProperty("CLLT_BRCH_NM")
        private String clltBrchNm;

        @JsonProperty("CLLT_LOC_CD")
        private String clltLocCd;

        @JsonProperty("CLLT_LOC_NM")
        private String clltLocNm;

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

        // 문서에 추가된 사용상태(사용/취소) 필드
        @JsonProperty("CPON_USE_STAT_CD")
        private String cponUseStatCd;

        @JsonProperty("CPON_USE_STAT_NM")
        private String cponUseStatNm;

        @JsonProperty("CPON_NO_OLD")
        private String cponNoOld;

        @JsonProperty("CLLT_DS")
        private String clltDs;
    }
}
