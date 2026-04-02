package kr.co.winnticket.integration.aquaplanet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AquaPlanetCouponHistoryRequest {

    @JsonProperty("CORP_CD")
    private String corpCd;

    @JsonProperty("CONT_NO")
    private String contNo;

    @JsonProperty("BSN_DATE")
    private String bsnDate;
}
