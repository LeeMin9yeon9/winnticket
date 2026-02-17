package kr.co.winnticket.integration.bankda.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BankOrderDetailResponse {

    private List<Order> order;

    @Data
    public static class Order {

        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("buyer_name")
        private String buyerName;

        @JsonProperty("billing_name")
        private String billingName;

        @JsonProperty("bank_account_no")
        private String bankAccountNo;

        @JsonProperty("bank_code_name")
        private String bankCodeName;

        @JsonProperty("order_price_amount")
        private int orderPriceAmount;

        @JsonProperty("order_date")
        private String orderDate;

        private List<Item> items;
    }

    @Data
    public static class Item {

        @JsonProperty("product_name")
        private String productName;
    }
}
