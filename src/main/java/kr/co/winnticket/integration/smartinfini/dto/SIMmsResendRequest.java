package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SIMmsResendRequest {

    @JsonProperty("type")
    private String type;     // 문서 예: "O"

    @JsonProperty("value")
    private String value;    // 문서 예: "testapi2020061007"

    @JsonProperty("user_hp")
    private String userHp;   // 없으면 "" 가능
}