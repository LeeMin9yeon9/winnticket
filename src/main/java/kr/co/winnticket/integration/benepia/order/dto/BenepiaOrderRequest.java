package kr.co.winnticket.integration.benepia.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class BenepiaOrderRequest {

    private String kcpCoCd;
    private String coopCoCd;
    private String benefitId;
    private String coCd;

    private Order order;
    private List<Payment> payments;
    private List<Product> products;

    @Data
    public static class Order {

        private String ordId;
        private String ordNm;

        private int ordPrc;
        private int orgnPrc;

        private String ordDt;

        private String ptnAccntId;

        private String ordDtlUrl;
        private String ordDtlUrlTyp;

        private String ordDtlUrlMobl;
        private String ordDtlUrlTypMobl;
    }

    @Data
    public static class Payment {

        private String sttlMeanId;
        private int sttlPrc;
    }

    @Data
    public static class Product {

        private String prdId;
        private String prdNm;
        private String prdOptNm;

        private int qty;

        private int prdPrc;
        private int prdOrgnPrc;

        private String prdDtlUrl;
        private String prdDtlUrlTyp;
        private String prdImgUrl;

        private String prdDtlUrlMobl;
        private String prdDtlUrlTypMobl;
        private String prdImgUrlMobl;

        private String prdType;

        private String prdGb;

        private String useFrDy;
        private String useToDy;
    }
}