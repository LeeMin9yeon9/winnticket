package kr.co.winnticket.integration.aquaplanet.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AquaplanetMessageHeader {

    @JsonProperty("MSG_PRCS_RSLT_CD")
    private String msgPrcsRsltCd; // 응답 시 0 정상 등

    @JsonProperty("MSG_DATA_SUB_RPTT_CNT")
    private Integer msgDataSubRpttCnt;

    @JsonProperty("MSG_DATA_SUB")
    private List<MessageItem> msgDataSub;

    @Data
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
