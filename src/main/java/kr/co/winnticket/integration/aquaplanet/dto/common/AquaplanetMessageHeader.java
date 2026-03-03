package kr.co.winnticket.integration.aquaplanet.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AquaplanetMessageHeader {

    @JsonProperty("MSG_PRCS_RSLT_CD")
    private String msgPrcsRsltCd; // "0" 정상

    @JsonProperty("MSG_DATA_SUB_RPTT_CNT")
    private Integer msgDataSubRpttCnt;

    @JsonProperty("MSG_DATA_SUB")
    private List<MessageItem> msgDataSub;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MessageItem {
        @JsonProperty("MSG_INDC_CD")
        private String msgIndcCd;

        @JsonProperty("MSG_CD")
        private String msgCd;

        @JsonProperty("MSG_CTNS")
        private String msgCtns;
    }
}
