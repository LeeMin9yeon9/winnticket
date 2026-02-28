package kr.co.winnticket.integration.aquaplanet.dto.contract;

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
public class APContractProductRequest {

    @JsonProperty("ds_search")
    private List<SearchItem> dsSearch;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchItem {

        @JsonProperty("CORP_CD")
        private String corpCd; // 법인코드

        @JsonProperty("CONT_NO")
        private String contNo; // 계약번호

        @JsonProperty("STDR_DATE")
        private String stdrDate; // 기준일자
    }
}
