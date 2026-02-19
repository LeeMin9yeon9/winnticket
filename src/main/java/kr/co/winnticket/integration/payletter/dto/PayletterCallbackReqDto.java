package kr.co.winnticket.integration.payletter.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Schema(title = "[Payletter 결제 성공 callback 요청 DTO] PayletterCallbackReqDto")
public class PayletterCallbackReqDto {

     // Payletter callback payload 전체 저장
     //"custom_parameter": "주문UUID",
     // "user_id": "01012345678",
     // "amount": "100",
     // "tid": "TESTTID123456",
     // "cid": "CID123456",
     // "payhash": "생성된해시"

    @Schema(description = "콜백 전체 payload")
    private Map<String, Object> payload = new HashMap<>();

    @JsonAnySetter
    public void put(String key, Object value) {
        payload.put(key, value);
    }

}
