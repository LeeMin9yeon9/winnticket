package kr.co.winnticket.integration.plusn.dto;

import lombok.Data;

@Data
public class PlusNCancelRequest {
    private String order_company;
    private String order_id;
    private String order_sales;
    private String result_date; // yyyyMMddHHmmss
}
