package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SIUseSearchResponse extends SIBaseResponse {

    @JsonProperty("order_no")
    private String orderNo;

    @JsonProperty("result_date")
    private String resultDate;
}