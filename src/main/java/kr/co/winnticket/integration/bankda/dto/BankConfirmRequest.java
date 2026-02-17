package kr.co.winnticket.integration.bankda.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class BankConfirmRequest {

    private List<Request> requests;

    @Data
    public static class Request {
        @JsonProperty("order_id")
        private String orderId;
    }
}


