package kr.co.winnticket.integration.bankda.dto;

import lombok.Data;
import java.util.List;

@Data
public class BankOrderResponse {

    private List<Order> orders;

    @Data
    public static class Order {

        private String order_id;
        private String buyer_name;
        private String billing_name;
        private String bank_account_no;
        private String bank_code_name;
        private int order_price_amount;
        private String order_date;

        private List<Item> items;
    }

    @Data
    public static class Item {
        private String product_name;
    }
}
