package kr.co.winnticket.integration.aquaplanet.dto.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class APContractProductResponse {

    @JsonProperty("ds_resultContPakgList")
    private List<Item> dsResultContPakgList;

    @Data
    public static class Item {
        @JsonProperty("GOODS_NO")
        private String goodsNo;

        @JsonProperty("GOODS_NM")
        private String goodsNm;

        @JsonProperty("TYPEID")
        private String typeId;

        @JsonProperty("TYPENM")
        private String typeNm;

        @JsonProperty("BRCH_CD")
        private String brchCd;

        @JsonProperty("BRCH_NM")
        private String brchNm;

        @JsonProperty("LOC_CD")
        private String locCd;

        @JsonProperty("LOC_NM")
        private String locNm;

        @JsonProperty("BDUJ_SESN_CD")
        private String bdujSesnCd;

        @JsonProperty("BDUJ_SESN_NM")
        private String bdujSesnNm;

        @JsonProperty("SALE_STRT_DATE")
        private String saleStrtDate;

        @JsonProperty("SALE_END_DATE")
        private String saleEndDate;

        @JsonProperty("APPLC_SM_AMT")
        private String applcSmAmt;
    }
}
