package kr.co.winnticket.integration.aquaplanet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AquaPlanetCancelResponse {

    @JsonProperty("RESULT_CODE")
    private String resultCode;

    @JsonProperty("RESULT_MSG")
    private String resultMsg;
}