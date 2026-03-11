package kr.co.winnticket.integration.aquaplanet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AquaPlanetRecallResponse {

    @JsonProperty("ds_result")
    private List<Result> dsResult;

    @Data
    public static class Result {

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

    public boolean isUsed() {
        if (dsResult == null || dsResult.isEmpty()) {
            return false;
        }

        String statCd = dsResult.get(0).getReprCponStatCd();
        return "30".equals(statCd) || "40".equals(statCd);
    }

    public boolean isCancelable() {
        if (dsResult == null || dsResult.isEmpty()) {
            return true;
        }

        String statCd = dsResult.get(0).getReprCponStatCd();
        return "10".equals(statCd) || "20".equals(statCd);
    }
}