package kr.co.winnticket.integration.benepia.order.dto;

import lombok.Data;
import java.util.List;

@Data
public class BenepiaCancelRequest {

    private String kcpCoCd; // kcp 코드
    private String coopCoCd; // 베네피아 제휴사 관리코드
    private String benefitId; // 베네피아 회원id
    private String coCd; // 베네피아 고객사 코드

    private OrderCancel orderCancel;
    private List<Payment> payments;
    private List<Product> products;

    @Data
    public static class OrderCancel {
        private String ordId; // 주문id
        private String ordNm; // 주문명
        private Integer cnclPrc; // 취소금액
        private Integer orgnCnclPrc; // 취소매입금액 0
        private String cnclDt; // 취소일시 (YYYYMMDDHHMISS)
    }

    @Data
    public static class Payment {
        private String sttlMeanId; // 결제수단 쿠폰:63, 포인트:10, 그외:9
        private Integer sttlPrc; // 결제금액
    }

    @Data
    public static class Product {
        private String prdId; // 상품id
        private String prdNm; // 상품명
        private String prdOptNm; // 상품옵션명
        private Integer qty; //  // 수량
        private Integer prdPrc; // 상품금액
        private Integer prdOrgnPrc; // 상품매입금액

        private String prdDtlUrl; // 상품상세페이지 url
        private String prdDtlUrlTyp; // 상품상세페이지접근형태 Y
        private String prdImgUrl; // 상품대표이미지 url

        private String prdDtlUrlMobl; // 상품상세페이지 모바일 url
        private String prdDtlUrlTypMobl; // 상품상세페이지접근형태 모바일 Y
        private String prdImgUrlMobl; // 상품대표이미지 모바일 url

        private String prdType; // 상품유형 10
        private String partCnclYn; // 부분취소여부 ""
    }
}