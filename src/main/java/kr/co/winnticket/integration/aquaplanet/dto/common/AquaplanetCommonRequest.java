package kr.co.winnticket.integration.aquaplanet.dto.common;

import lombok.*;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AquaplanetCommonRequest {
    private SystemHeader SystemHeader;
    private TransactionHeader TransactionHeader;
    private Object MessageHeader; // 빈 객체 {} 송신
    private Map<String, List<?>> Data;

    @Data
    @Builder
    public static class SystemHeader {
        private String TMSG_VER_DV_CD;    // "01"
        private String ENVR_INFO_DV_CD;   // "D"
        private String STN_MSG_ENCP_CD;   // "0"
        private String STN_MSG_COMP_CD;   // "0"
        private String LANG_CD;           // "KO"
        private String TMSG_WRTG_DT;      // YYYYMMDD
        private String TMSG_CRE_SYS_NM;   // SIF + Random(5)
        private String STD_TMSG_SEQ_NO;   // Random(1) + unix time(13)
        private String STD_TMSG_PRGR_NO;  // "00"
        private String STN_TMSG_IP;       // 13.109.91.167
        private String FRS_RQST_SYS_CD;   // "SIF"
        private String FRS_RQST_DTM;      // YYYYMMDDHHMMSSTTT
        private String TRMS_SYS_CD;       // "SIF"
        private String RQST_RSPS_DV_CD;   // "S"
        private String TRSC_SYNC_DV_CD;   // "S"
        private String TMSG_RQST_DTM;     // YYYYMMDDHHMMSSTTT
        private String RECV_SVC_CD;
        private String INTF_ID;
    }

    @Data
    @Builder
    public static class TransactionHeader {
        private String STN_MSG_TR_TP_CD;  // "O"
        private String SYSTEM_TYPE;       // "HABIS"
        private String CORP_CD;           // 4000, 5000 등
        private String CMP_NO;            // 회사코드 (보통 법인코드와 동일하게 세팅)
        private String WRKR_NO;           // l1711019
        private String MASK_AUTH;         // "0"
    }
}