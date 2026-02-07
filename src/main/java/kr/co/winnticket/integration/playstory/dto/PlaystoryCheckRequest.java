package kr.co.winnticket.integration.playstory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PlaystoryCheckRequest {

    @JsonProperty("CHN_ID")
    private String chnId;   // 판매채널 ID (P240076)

    @JsonProperty("OPT_LIST")
    private List<OptChk> optList;

    @Data
    public static class OptChk {
        @JsonProperty("CPN_NO")
        private String cpnNo;
    }
}
