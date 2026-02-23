package kr.co.winnticket.integration.plusn.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class PlusNCancelResponse extends PlusNBaseResponse {
    private String order_id;
    private String order_sales;
}