package kr.co.winnticket.integration.playstory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PlaystoryCheckResponse {

    @JsonProperty("OPT_LIST")
    private List<OptChkResult> optList;

    @Data
    public static class OptChkResult {

        @JsonProperty("result_code")
        private Integer resultCode;

        @JsonProperty("CPN_NO")
        private String cpnNo;

        @JsonProperty("result_message")
        private String resultMessage;
    }
}
