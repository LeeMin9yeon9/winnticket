package kr.co.winnticket.integration.lscompany.dto;

import lombok.Data;

@Data
public class LsOrderTicket {
    private String orderNumber; // 주문번호

    private String ticketNumber; // 티켓번호

    private boolean ticketUsed; // 티켓사용여부
}
