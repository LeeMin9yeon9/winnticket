package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class SIProductRequest {

    @JsonProperty("type")
    private String type; // 문서: "A" 등

    @JsonProperty("value")
    private String value;

    @JsonProperty("valid_date1")
    private String validDate1;

    @JsonProperty("valid_date2")
    private String validDate2;
}