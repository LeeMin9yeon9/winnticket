package kr.co.winnticket.integration.woongjin.dto;

import lombok.Data;

@Data
public class WJCancelResponse {
    private String code; // 응답코드
    private String message; // 응답메세지
    private Object data; // 항상 null
}
