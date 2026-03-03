package kr.co.winnticket.integration.aquaplanet.dto.coupon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class APCouponUseDailyHistoryRequest {

    @JsonProperty("ds_input")
    private List<Input> dsInput;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Input {
        @JsonProperty("CORP_CD")
        private String corpCd;

        @JsonProperty("CONT_NO")
        private String contNo;

        @JsonProperty("BSN_DATE")
        private String bsnDate; // yyyyMMdd
    }
}
