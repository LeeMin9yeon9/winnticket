package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SISearchResponse extends SIBaseResponse {

    // 문서 예시: result_date: "20200514130605" 또는 "1970-01-01 00:00:00"
    @JsonProperty("result_date")
    private String resultDate;
}