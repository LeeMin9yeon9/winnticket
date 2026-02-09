package kr.co.winnticket.integration.bankda.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class BankConfirmResponse {

    private int return_code;
    private String description;
    private List<OrderResult> orders;

    @Data
    @Builder
    public static class OrderResult {
        private String order_id;
        private String description;
    }
}
