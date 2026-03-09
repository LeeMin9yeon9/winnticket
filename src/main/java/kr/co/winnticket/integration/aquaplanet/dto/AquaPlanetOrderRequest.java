package kr.co.winnticket.integration.aquaplanet.dto;

import lombok.Data;

import java.util.List;

@Data
public class AquaPlanetOrderRequest {
    private String order_no;
    private String user_name;
    private String user_phone;
    private List<Item> items;

    @Data
    public static class Item {
        private String goods_no;
        private Integer quantity;
        private String corp_cd;
        private String cont_no;
    }
}