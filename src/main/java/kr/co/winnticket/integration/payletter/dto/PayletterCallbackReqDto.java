package kr.co.winnticket.integration.payletter.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Schema(title = "[Payletter 결제 성공 callback 요청 DTO] PayletterCallbackReqDto")
public class PayletterCallbackReqDto {

    @Schema(description = "콜백 전체 payload")
    private Map<String, Object> payload = new HashMap<>();

    @JsonAnySetter
    public void put(String key, Object value) {
        payload.put(key, value);
    }

}
