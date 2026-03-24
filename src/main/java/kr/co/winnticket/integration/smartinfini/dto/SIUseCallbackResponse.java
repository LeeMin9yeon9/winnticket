package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SIUseCallbackResponse {

    @JsonProperty("order_no")
    private final String orderNo;

    @JsonProperty("return_div")
    private final String returnDiv;

    @JsonProperty("return_msg")
    private final String returnMsg;
}