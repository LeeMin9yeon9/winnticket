package kr.co.winnticket.integration.benepia.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class BenepiaOrderRequest {

    private String kcpCoCd; // KCP 코드
    private String coopCoCd; // 베네피아 제휴사관리코드
    private String benefitId; // 베네피아 ID
    private String coCd; // 베네피아고객사코드

    private Order order;
    private List<Payment> payments;
    private List<Product> products;

    @Data
    public static class Order {
        private String ordId; // 주문ID
        private String ordNm; // 주문명
        private Integer ordPrc; // 주문금액
        private Integer orgnPrc; // 매입금액 0
        private String ordDt; // 주문일시 (YYYYMMDDHHMISS)
        private String ptnAccntId; // 제휴사계정ID

        private String ordDtlUrl; // 주문상세 사이트 url
        private String ordDtlUrlTyp; // 주문상세 사이트 접근유형 Y
        private String ordDtlUrlMobl; // 주문상세 모바일 url
        private String ordDtlUrlTypMobl; // 주문상세 사이트 접근유형 모바일 Y
    }

    @Data
    public static class Payment {
        private String sttlMeanId; // 결제수단ID 쿠폰 63, 베네피아 포인트 10, 그외 기타 9
        private int sttlPrc; // 결제금액
    }

    @Data
    public static class Product {
        private String prdId; // 상품ID
        private String prdNm; // 상품명
        private String prdOptNm; // 상품옵션명
        private int qty; // 상품수량
        private int prdPrc; // 상품금액
        private Integer prdOrgnPrc; // 상품매입금액 0

        private String prdDtlUrl; // 상품상세페이지 url
        private String prdDtlUrlTyp; // 상품상세페이지 접근 형태 Y
        private String prdImgUrl; // 상품대표이미지 url

        private String prdDtlUrlMobl; // 상품상세페이지 모바일 url
        private String prdDtlUrlTypMobl; // 상품상세페이지 접근형태 모바일 Y
        private String prdImgUrlMobl; // 상품 대표 이미지 모바일 url

        private String prdType; // 상품유형 10
        private String prdGb; // 상품 타입 03

        private String useFrDy; // ""
        private String useToDy; // ""

        private String roomTypNm; // ""
        private Integer adultCnt; // ""
        private Integer youthCnt; // ""
        private Integer childCnt; // ""
        private Integer nightCnt; // ""
        private String weekendYn; // ""
        private String seasonYn; // ""
        private String repResvNm; //""
        private List<AirResvInfo> airResvInfoList;
    }

    @Data
    public static class AirResvInfo {
        private String resvType; // ""
        private String resvNo; // ""
        private String ticketNo; // ""
        private String depPointCity; // ""
        private String depPointNation; // ""
        private String destPointCity; // ""
        private String destPointNation; // ""
        private String airDt; // ""
        private String airline; // ""
        private String seatClass; // ""
        private String passengerTyp; // ""
    }
}