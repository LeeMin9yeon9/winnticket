package kr.co.winnticket.integration.aquaplanet.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class AquaPlanetIssueRequest {

    @JsonIgnore
    private UUID ticketId;

    @JsonProperty("CORP_CD")
    private String corpCd;

    @JsonProperty("CONT_NO")
    private String contNo;

    @JsonProperty("SEQ")
    private Integer seq;

    @JsonProperty("ISSUE_DATE")
    private String issueDate;

    @JsonProperty("GOODS_NO")
    private String goodsNo;

    @JsonProperty("ISSUE_QTY")
    private Integer issueQty;

    @JsonProperty("UNITY_ISSUE_YN")
    private String unityIssueYn;

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