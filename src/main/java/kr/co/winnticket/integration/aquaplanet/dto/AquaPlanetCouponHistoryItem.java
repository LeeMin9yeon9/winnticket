package kr.co.winnticket.integration.aquaplanet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AquaPlanetCouponHistoryItem {

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

    @JsonProperty("CPON_USE_STAT_CD")
    private String cponUseStatCd;

    @JsonProperty("CPON_USE_STAT_NM")
    private String cponUseStatNm;

    @JsonProperty("CLLT_DS")
    private String clltDs;
}
