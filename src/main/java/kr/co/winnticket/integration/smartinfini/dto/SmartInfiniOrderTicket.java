package kr.co.winnticket.integration.smartinfini.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SmartInfiniOrderTicket {
    private String orderNumber; // 주문번호

    private String ticketNumber; // 티켓번호

    private boolean ticketUsed; // 티켓사용여부
}
