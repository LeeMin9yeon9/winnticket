package kr.co.winnticket.integration.aquaplanet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AquaPlanetOrderResponse {
    private Map<String, Object> SystemHeader;
    private Map<String, Object> MessageHeader;
    private DataBody Data;

    @Data
    public static class DataBody {
        private List<Coupon> ds_output;
    }

    @Data
    public static class Coupon {
        @JsonProperty("REPR_CPON_INDICT_NO")
        private String repr_cpon_indict_no; // 한화 쿠폰번호
        @JsonProperty("CPON_NO")
        private String cpon_no;
    }
}