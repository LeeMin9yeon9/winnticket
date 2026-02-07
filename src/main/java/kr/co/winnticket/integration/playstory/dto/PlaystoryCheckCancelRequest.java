package kr.co.winnticket.integration.playstory.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PlaystoryCheckCancelRequest {
    @JsonProperty("CHN_ID")
    private String chnId;

    @JsonProperty("OPT_LIST")
    private List<OptCanc> optList;

    @Data
    public static class OptCanc {
        @JsonProperty("CPN_NO")
        private String cpnNo;
    }
}
