package kr.co.winnticket.integration.plusn.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlusNUsedDateResponse {

    private List<UsedOrder> used_list;

    @Data
    public static class UsedOrder {

        private String order_id;
        private List<Coupon> coupon;

    }

    @Data
    public static class Coupon {

        private String status_div;
        private String coupon_no;
        private String order_sales;
        private String status_msg;
        private String result_date;

    }
}
