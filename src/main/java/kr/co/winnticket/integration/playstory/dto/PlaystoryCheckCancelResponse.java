package kr.co.winnticket.integration.playstory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PlaystoryCheckCancelResponse {
    @JsonProperty("OPT_LIST")
    private List<OptCancResult> optList;

    @Data
    public static class OptCancResult {
        @JsonProperty("result_code")
        private String resultCode;

        @JsonProperty("result_message")
        private String resultMessage;

        @JsonProperty("CPN_NO")
        private String cpnNo;
    }
}
