package kr.co.winnticket.integration.woongjin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WJResendResponse {
    private String code; // 응답코드
    private String message; // 응답 메세지
    private Object data; // 항상 null
}