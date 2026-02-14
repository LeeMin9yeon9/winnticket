package kr.co.winnticket.integration.aquaplanet.dto.coupon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class APCouponCancelResponse {

    @JsonProperty("ds_output")
    private List<Output> dsOutput;

    @Data
    public static class Output {
        @JsonProperty("RESULT_CODE")
        private String resultCode;

        @JsonProperty("RESULT_MSG")
        private String resultMsg;
    }
}
