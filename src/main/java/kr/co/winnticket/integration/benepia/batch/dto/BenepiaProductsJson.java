package kr.co.winnticket.integration.benepia.batch.dto;

import lombok.Data;
import java.util.List;

@Data
public class BenepiaProductsJson {
    private List<ProductWrap> products;

    @Data
    public static class ProductWrap {
        private Product product;
        private Travel travel;
        private Ticket ticket;
    }

    @Data public static class Product {
        public String prdId, prdNm, prdImgUrl;
        public int orgnPrc, salePrc;
        public String prdDtlUrlTyp, prdDtlUrl;
        public String prdMobDtlUrlTyp, prdMobDtlUrl;
        public String keyword, prdType, prdSubTitle, prdDesc;
        public String regDate, updDate;
        public String param1, param2, param3;
    }

    @Data public static class Travel {
        public String prdGb, nationalCd, regionCd;
        public String districCd="", telNo="", zipCd="", addr1="", addr2="", homepage="";
        public int xPoint=0, yPoint=0;
    }

    @Data public static class Ticket {
        public String ticketType, expireInfo, ticketPlace;
    }
}
