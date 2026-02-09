package kr.co.winnticket.integration.bankda.dto;

import lombok.Data;
import java.util.List;

@Data
public class BankConfirmRequest {

    private List<ConfirmItem> requests;

    @Data
    public static class ConfirmItem {
        private String order_id;
    }
}

