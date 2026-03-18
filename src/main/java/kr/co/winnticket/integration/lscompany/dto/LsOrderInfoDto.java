package kr.co.winnticket.integration.lscompany.dto;

import lombok.Data;

import java.util.UUID;

@Data
// 주문 기본 정보
public class LsOrderInfoDto {

    private UUID orderId;

    private String orderNumber;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private String paymentStatus;

    private String orderStatus;



}
