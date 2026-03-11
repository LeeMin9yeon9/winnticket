package kr.co.winnticket.integration.aquaplanet.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AquaPlanetCancelRequest {

    @JsonIgnore
    private Long ticketId;

    @JsonProperty("CORP_CD")
    private String corpCd;

    @JsonProperty("CONT_NO")
    private String contNo;

    @JsonProperty("REPR_CPON_INDICT_NO")
    private String reprCponIndictNo;
}