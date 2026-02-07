package kr.co.winnticket.integration.woongjin.dto;

import lombok.Data;

import java.util.List;

@Data
public class WJOrderInquiryResponse {
    private String code; // 응답코드
    private String message; // 응답메세지
    private List<WJOrderResponse.DataBlock> data; // 응답 데이터

    @Data
    public static class DataBlock {
        private Integer reservation_id; // 주문 pk
        private String channel_order_number; // 채널주문번호
        private String customer_name; // 주문자이름
        private String customer_phone; // 주문자 휴대폰 번호
        private String customer_email; // 주문자 이메일
        private List<WJOrderResponse.Product> products; // 상품리스트
        private String created_at; // 주문등록일시
        private String updated_at; // 주문수정일시
    }

    @Data
    public static class Product {
        private Integer product_id; // 주문상품 pk
        private String product_channel_order_number; // 채널상품주문번호 (우리기준 티켓번호, 실 사용 티켓번호는 핀번호)
        private String product_state;   // ORDER:구매완료 / RESERVED:예약완료 / COMPLETE:사용완료 / CANCEL:구매취소
        private Boolean is_used; // 사용여부
        private Boolean is_canceled; // 취소여부
        private String used_at;          // 사용일시
        private String canceled_at;      // 취소일시
        private String pin; // 핀번호
    }
}
