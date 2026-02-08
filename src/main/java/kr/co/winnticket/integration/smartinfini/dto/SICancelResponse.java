package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SICancelResponse extends SIBaseResponse {
    @JsonProperty("order_sales")
    private String orderSales;
}