package kr.co.winnticket.integration.smartinfini.dto;

import lombok.Data;

import java.util.List;

@Data
public class SICancelResponse {
    private String returnDiv; // 성공여부
    private String orderNo; // 주문번호
    private String gcoupon; // 그룹쿠폰번호
    private String returnMsg; // 결과메세지
    private List<Coupon> cpoupon;

    @Data
    public static class Coupon {
        private String ticketCode; // 티켓번호
        private String orderSales; // 판매가격
        private String couponNo; // 쿠폰번호
    }
}
