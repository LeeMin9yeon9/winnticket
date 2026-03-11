package kr.co.winnticket.integration.aquaplanet.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class AquaPlanetRecallRequest {

    @JsonIgnore
    private UUID ticketId;

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
}