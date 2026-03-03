package kr.co.winnticket.integration.aquaplanet.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AquaplanetSystemHeader {

    @JsonProperty("STD_TMSG_LEN")
    private String stdTmsgLen;

    @JsonProperty("TMSG_VER_DV_CD")
    private String tmsgVerDvCd;

    @JsonProperty("ENVR_INFO_DV_CD")
    private String envrInfoDvCd;

    @JsonProperty("STN_MSG_ENCP_CD")
    private String stnMsgEncpCd;

    @JsonProperty("STN_MSG_COMP_CD")
    private String stnMsgCompCd;

    @JsonProperty("LANG_CD")
    private String langCd;

    @JsonProperty("TMSG_WRTG_DT")
    private String tmsgWrtgDt; // yyyyMMdd

    @JsonProperty("TMSG_CRE_SYS_NM")
    private String tmsgCreSysNm; // SIF + Random(5)

    @JsonProperty("STD_TMSG_SEQ_NO")
    private String stdTmsgSeqNo; // Random(1)+unixtime(13) 권장

    @JsonProperty("STD_TMSG_PRGR_NO")
    private String stdTmsgPrgrNo; // "00"

    @JsonProperty("STN_TMSG_IP")
    private String stnTmsgIp;

    @JsonProperty("STN_TMSG_MAC")
    private String stnTmsgMac;

    @JsonProperty("FRS_RQST_SYS_CD")
    private String frsRqstSysCd; // "SIF"

    @JsonProperty("FRS_RQST_DTM")
    private String frsRqstDtm; // yyyyMMddHHmmssSSS

    @JsonProperty("TRMS_SYS_CD")
    private String trmsSysCd;

    @JsonProperty("TRMS_ND_NO")
    private String trmsNdNo;

    @JsonProperty("RQST_RSPS_DV_CD")
    private String rqstRspsDvCd; // S

    @JsonProperty("TRSC_SYNC_DV_CD")
    private String trscSyncDvCd; // S

    @JsonProperty("TMSG_RQST_DTM")
    private String tmsgRqstDtm; // yyyyMMddHHmmssSSS

    @JsonProperty("RECV_SVC_CD")
    private String recvSvcCd; // HBSSAM...

    @JsonProperty("INTF_ID")
    private String intfId; // SIF00...

    // response fields (optional)
    @JsonProperty("TMSG_RSPS_DTM")
    private String tmsgRspsDtm;

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
