package kr.co.winnticket.integration.woongjin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WJProductListResponse {
    private String code;
    private String message;
    private List<Map<String, Object>> data;
}
