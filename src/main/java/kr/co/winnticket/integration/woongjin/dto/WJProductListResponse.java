package kr.co.winnticket.integration.woongjin.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class WJProductListResponse {
    private String code;
    private String message;
    private List<Map<String, Object>> data;
}
