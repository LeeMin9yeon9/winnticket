package kr.co.winnticket.integration.aquaplanet.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AquaplanetTransactionHeader {

    @JsonProperty("STN_MSG_TR_TP_CD")
    private String stnMsgTrTpCd; // fix "O"

    @JsonProperty("SYSTEM_TYPE")
    private String systemType; // fix "HABIS"

    @JsonProperty("SCREEN_SHORTEN_NO")
    private String screenShortenNo;

    @JsonProperty("SCREEN_ID")
    private String screenId;

    @JsonProperty("CORP_CD")
    private String corpCd;

    @JsonProperty("CMP_NO")
    private String cmpNo;

    @JsonProperty("BRANCH_NO")
    private String branchNo;

    @JsonProperty("LOC_CD")
    private String locCd;

    @JsonProperty("WRKR_NO")
    private String wrkrNo; // 문서 example에 값 있음

    @JsonProperty("PERS_INFO_MASK")
    private String persInfoMask;

    @JsonProperty("MASK_AUTH")
    private String maskAuth; // fix "0"

    @JsonProperty("OSDE_TR_CD")
    private String osdeTrCd;

    @JsonProperty("OSDE_TR_ORG_CD")
    private String osdeTrOrgCd;

    @JsonProperty("OSDE_TR_MSG_CD")
    private String osdeTrMsgCd;

    @JsonProperty("OSDE_TR_JOB_CD")
    private String osdeTrJobCd;

    @JsonProperty("OSDE_TR_RUTN_ID")
    private String osdeTrRutnId;

    @JsonProperty("OSDE_TR_PRG_NO")
    private String osdeTrPrgNo;

    @JsonProperty("FILLER")
    private String filler;
}
