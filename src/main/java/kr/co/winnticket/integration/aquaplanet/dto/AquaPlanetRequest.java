package kr.co.winnticket.integration.aquaplanet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AquaPlanetRequest<T> {
    @JsonProperty("SystemHeader")
    private SystemHeader systemHeader;
    @JsonProperty("TransactionHeader")
    private TransactionHeader transactionHeader;
    @JsonProperty("MessageHeader")
    private MessageHeader messageHeader = new MessageHeader();
    @JsonProperty("Data")
    private T data;

    @Data
    public static class SystemHeader {
        private String STD_TMSG_LEN = "";
        private String TMSG_VER_DV_CD = "01";
        private String ENVR_INFO_DV_CD = "D"; // D:개발, R:운영
        private String STN_MSG_ENCP_CD = "0";
        private String STN_MSG_COMP_CD = "0";
        private String LANG_CD = "KO";
        private String TMSG_WRTG_DT;
        private String TMSG_CRE_SYS_NM;
        private String STD_TMSG_SEQ_NO;
        private String STD_TMSG_PRGR_NO = "00";
        private String STN_TMSG_IP;
        private String STN_TMSG_MAC = "";
        private String FRS_RQST_SYS_CD = "SIF";
        private String FRS_RQST_DTM;
        private String TRMS_SYS_CD = "SIF";
        private String TRMS_ND_NO = "";
        private String RQST_RSPS_DV_CD = "S";
        private String TRSC_SYNC_DV_CD = "S";
        private String TMSG_RQST_DTM;
        private String RECV_SVC_CD;
        private String INTF_ID;
    }

    @Data
    public static class TransactionHeader {
        private String STN_MSG_TR_TP_CD = "O";
        private String SYSTEM_TYPE = "HABIS";
        private String SCREEN_ID = "";
        private String CORP_CD = "1000";
        private String CMP_NO = "";
        private String WRKR_NO = "l1711019";
        private String MASK_AUTH = "0";
    }

    @Data
    public static class MessageHeader {
        private String MSG_PRCS_RSLT_CD = "";
        private Integer MSG_DATA_SUB_RPTT_CNT = 0;
    }
}