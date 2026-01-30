package kr.co.winnticket.integration.mair.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
// 엠에어쿠폰 API 응답 JSON 매핑 DTO
public class MairCouponResDto {

    @JsonProperty("result")
    private String result; // 결과값

    @JsonProperty("TNO")
    private String tno;  // 발송 성공 시 쿠폰번호

    @JsonProperty("TRNO")
    private String trno; // 취소 성공 시 거래번호

    public boolean isOk(){
        return  "OK".equalsIgnoreCase(result);
    }


}
