package kr.co.winnticket.integration.bankda.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BankOrderDetailRequest {

    @JsonProperty("order_id")
    private String orderId;

}
