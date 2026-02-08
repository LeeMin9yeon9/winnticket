package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SIBaseResponse {
    @JsonProperty("return_div")
    private String returnDiv;

    @JsonProperty("return_msg")
    private String returnMsg;
}