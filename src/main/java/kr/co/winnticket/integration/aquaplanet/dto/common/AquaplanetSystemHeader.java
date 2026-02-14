package kr.co.winnticket.integration.aquaplanet.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AquaplanetSystemHeader {

    @JsonProperty("STD_TMSG_LEN")
    private String stdTmsgLen; // 전체길이: 8byte (JSON은 null/"" 가능, 문서에 “JSON 생략”이라고 되어있으면 비워둬도 됨)

    @JsonProperty("TMSG_VER_DV_CD")
    private String tmsgVerDvCd; // fix "01"

    @JsonProperty("ENVR_INFO_DV_CD")
    private String envrInfoDvCd; // variable (D/R)

    @JsonProperty("STN_MSG_ENCP_CD")
    private String stnMsgEncpCd; // fix "0"

    @JsonProperty("STN_MSG_COMP_CD")
    private String stnMsgCompCd; // fix "0"

    @JsonProperty("LANG_CD")
    private String langCd; // fix "KO"

    @JsonProperty("TMSG_WRTG_DT")
    private String tmsgWrtgDt; // YYYYMMDD

    @JsonProperty("TMSG_CRE_SYS_NM")
    private String tmsgCreSysNm; // "SIF" + Random(5)

    @JsonProperty("STD_TMSG_SEQ_NO")
    private String stdTmsgSeqNo; // Random(1) + unix time(13)

    @JsonProperty("STD_TMSG_PRGR_NO")
    private String stdTmsgPrgrNo; // fix "00"

    @JsonProperty("STN_TMSG_IP")
    private String stnTmsgIp; // variable

    @JsonProperty("STN_TMSG_MAC")
    private String stnTmsgMac; // variable

    @JsonProperty("FRS_RQST_SYS_CD")
    private String frsRqstSysCd; // fix "SIF"

    @JsonProperty("FRS_RQST_DTM")
    private String frsRqstDtm; // YYYYMMDDHHMMSSSSS(17)

    @JsonProperty("TRMS_SYS_CD")
    private String trmsSysCd; // fix "SIF" (문서상 TRMS_SYS_CD fix SIF)

    @JsonProperty("TRMS_ND_NO")
    private String trmsNdNo; // none/variable

    @JsonProperty("RQST_RSPS_DV_CD")
    private String rqstRspsDvCd; // fix "S"(요청) / R(응답) 같은식이면 요청은 S

    @JsonProperty("TRSC_SYNC_DV_CD")
    private String trscSyncDvCd; // fix "S"

    @JsonProperty("TMSG_RQST_DTM")
    private String tmsgRqstDtm; // YYYYMMDDHHMMSSSSS(17)

    @JsonProperty("RECV_SVC_CD")
    private String recvSvcCd; // 서비스아이디(문서: 계약사 상품조회 등)

    @JsonProperty("INTF_ID")
    private String intfId; // 인터페이스ID (문서: SIF00... )

    @JsonProperty("TMSG_RSPS_DTM")
    private String tmsgRspsDtm; // 응답시각(응답일 때 채워짐)

    @JsonProperty("PRCS_RSLT_CD")
    private String prcsRsltCd;

    @JsonProperty("ERR_OCC_SYS_CD")
    private String errOccSysCd;

    @JsonProperty("STN_TMSG_ERR_CD")
    private String stnTmsgErrCd;

    @JsonProperty("MCI_NODE_NO")
    private String mciNodeNo;

    @JsonProperty("REMT_IP")
    private String remtIp;

    @JsonProperty("MCI_SSN_ID")
    private String mciSsnId;

    @JsonProperty("FILLER")
    private String filler;
}
