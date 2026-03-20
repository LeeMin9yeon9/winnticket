package kr.co.winnticket.integration.benepia.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class BenepiaOrderBatchRequest {
    private String header = "H";   // 문서 고정값
    private String tail = "T";     // 문서 고정값
    private List<Object> body;     // 주문 + 취소
}
