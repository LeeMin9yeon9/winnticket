package kr.co.winnticket.order.admin.dto;

import lombok.Data;

@Data
public class OrderExportResDto {
    private String channelName;       // 채널명
    private String orderedAt;         // 주문일
    private String orderNumber;       // 주문번호
    private String companyName;       // 회사명
    private String departmentName;    // 부서명
    private String customerName;      // 주문자 이름
    private String customerPhone;     // 주문자 전화번호
    private String customerEmail;     // 주문자 이메일
    private String ordererZipcode;    // 주문자 우편번호
    private String ordererAddress;    // 주문자 주소
    private String recipientName;     // 수령자 이름
    private String recipientZipcode;  // 수령자 우편번호
    private String recipientAddress;  // 수령자 주소
    private String recipientPhone;    // 수령자 전화번호
    private String productCode;       // 상품번호
    private String productDisplayName;// 주문상품
    private String reservationDate;   // 예약일자
    private String productCategory;   // 상품종류
    private String ticketType;        // 티켓종류
    private Integer quantity;         // 수량
    private Integer unitPrice;        // 단가
    private Integer supplyPrice;      // 공급가
    private Integer shippingFee;      // 배송비
    private Integer totalOrderAmount; // 총 주문금액
    private String paymentStatus;     // 결제상태
    private Integer finalPrice;       // 결제금액
    private String paymentMethod;     // 결제수단
    private Integer pointAmount;      // 베네피아 포인트 결제금액
    private String benepiaId;         // 베네피아 아이디
    private Integer bankTransferAmount; // 무통장 결제금액
    private Integer cardAmount;       // 신용카드 결제금액
    private String voucherInfo;       // 이용권
}
