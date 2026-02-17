package kr.co.winnticket.integration.bankda.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankConfirmResponse {

    @JsonProperty("return_code")
    private int returnCode;

    private String description;

    private List<OrderResult> orders;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderResult {

        @JsonProperty("order_id")
        private String orderId;

        private String description;
    }
}

