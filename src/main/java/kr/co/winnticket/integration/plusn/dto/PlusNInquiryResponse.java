package kr.co.winnticket.integration.plusn.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class PlusNInquiryResponse extends PlusNBaseResponse {
    private String return_div;
    private String return_msg;
    private String order_sales;
    private String result_date;
}
