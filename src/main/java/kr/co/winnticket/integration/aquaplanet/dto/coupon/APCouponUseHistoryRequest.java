package kr.co.winnticket.integration.aquaplanet.dto.coupon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class APCouponUseHistoryRequest {

    @JsonProperty("ds_input")
    private List<Input> dsInput;

    @Data
    @Builder
    public static class Input {
        @JsonProperty("CORP_CD")
        private String corpCd;

        @JsonProperty("CONT_NO")
        private String contNo;

        @JsonProperty("ISSUE_DATE")
        private String issueDate; // YYYYMMDD

        @JsonProperty("REPR_CPON_SEQ")
        private String reprCponSeq;

        @JsonProperty("REPR_CPON_INDICT_NO")
        private String reprCponIndictNo;
    }
}
