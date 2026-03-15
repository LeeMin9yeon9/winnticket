package kr.co.winnticket.integration.benepia.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class BenepiaCancelRequest {

    private String kcpCoCd;
    private String coopCoCd;
    private String benefitId;
    private String coCd;

    private OrderCancel orderCancel;
    private List<Payment> payments;
    private List<Product> products;

    @Data
    public static class OrderCancel {

        private String ordId;
        private String ordNm;

        private int cnclPrc;
        private int orgnCnclPrc;

        private String cnclDt;
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

        private int qty;

        private int prdPrc;
        private int prdOrgnPrc;

        private String prdType;

        private String partCnclYn;
    }
}