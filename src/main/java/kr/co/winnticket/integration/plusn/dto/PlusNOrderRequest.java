package kr.co.winnticket.integration.plusn.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlusNOrderRequest {
    private String order_id;
    private String user_name;
    private String user_hp;
    private String user_email;
    private String order_date; // yyyyMMddHHmmss
    private List<ClassDiv> class_div;

    @Data
    public static class ClassDiv {
        private String gubun;         // A/B
        private String goods_code;    // 상품코드
        private String cnt;           // 수량(문서상 String, 1 고정)
        private String selected_date; // yyyy-MM-dd or ""
    }
}