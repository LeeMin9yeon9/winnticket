package kr.co.winnticket.integration.aquaplanet.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AquaPlanetCouponHistoryResponse {

    @JsonProperty("ds_result")
    private List<AquaPlanetCouponHistoryItem> dsResult;
}
