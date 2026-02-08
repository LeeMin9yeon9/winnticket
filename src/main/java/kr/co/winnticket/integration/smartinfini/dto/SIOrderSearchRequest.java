package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SIOrderSearchRequest {

    @JsonProperty("order_no")
    private String orderNo;
}