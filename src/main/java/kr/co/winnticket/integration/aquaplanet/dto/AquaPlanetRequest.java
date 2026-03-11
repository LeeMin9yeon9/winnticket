package kr.co.winnticket.integration.aquaplanet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AquaPlanetRequest<T> {
    // 공통 요청 구조
    @Data
    public static class Request<T> {
        @JsonProperty("SystemHeader")
        private SystemHeader systemHeader;
        @JsonProperty("TransactionHeader")
        private TransactionHeader transactionHeader;
        @JsonProperty("MessageHeader")
        private Object messageHeader = new Object();
        @JsonProperty("Data")
        private T data;
    }

    @Data @Builder
    public static class SystemHeader {
        private String TMSG_VER_DV_CD = "01";
        private String ENVR_INFO_DV_CD = "D";
        private String STN_MSG_ENCP_CD = "0";
        private String STN_MSG_COMP_CD = "0";
        private String LANG_CD = "KO";
        private String TMSG_WRTG_DT; // yyyyMMdd
        private String TMSG_CRE_SYS_NM = "SIF99999";
        private String STD_TMSG_SEQ_NO; // 유니크값
        private String STD_TMSG_PRGR_NO = "00";
        private String STN_TMSG_IP = "13.109.91.167";
        private String STN_TMSG_MAC = "00-00-00-00-00-00";
        private String FRS_RQST_SYS_CD = "SIF";
        private String FRS_RQST_DTM; // yyyyMMddHHmmssSSS
        private String TRMS_SYS_CD = "SIF";
        private String RQST_RSPS_DV_CD = "S";
        private String TRSC_SYNC_DV_CD = "S";
        private String TMSG_RQST_DTM;
        private String RECV_SVC_CD;
        private String INTF_ID;
    }

    @Data @Builder
    public static class TransactionHeader {
        private String STN_MSG_TR_TP_CD = "O";
        private String SYSTEM_TYPE = "HABIS";
        private String WRKR_NO = "l1711019";
        private String MASK_AUTH = "0";
        private String CORP_CD;
    }

    // 각 서비스별 Data 영역 DTO들
    @Data public static class IssueData { @JsonProperty("ds_input") private List<Map<String, Object>> dsInput; }
    @Data public static class CancelData { @JsonProperty("ds_input") private List<Map<String, Object>> dsInput; }
    @Data public static class SearchData { @JsonProperty("ds_search") private List<Map<String, Object>> dsSearch; }
}