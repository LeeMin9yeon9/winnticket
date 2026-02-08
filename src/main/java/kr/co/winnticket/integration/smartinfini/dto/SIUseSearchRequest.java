package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SIUseSearchRequest {

    @JsonProperty("ticket_code")
    private final String ticketCode; // 문서상 ticket_code 기준으로 변경됨
}