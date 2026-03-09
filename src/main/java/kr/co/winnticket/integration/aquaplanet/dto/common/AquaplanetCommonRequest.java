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
    private Map<String, Object> MessageHeader; // 빈 객체 {} 필수
    private Map<String, List<Map<String, Object>>> Data;

    @Data @Builder
    public static class SystemHeader {
        private final String TMSG_VER_DV_CD = "01";
        private String ENVR_INFO_DV_CD;   // "D" (개발) or "R" (운영)
        private final String STN_MSG_ENCP_CD = "0";
        private final String STN_MSG_COMP_CD = "0";
        private final String LANG_CD = "KO";
        private String TMSG_WRTG_DT;      // YYYYMMDD
        private String TMSG_CRE_SYS_NM;   // SIF + Random(5)
        private String STD_TMSG_SEQ_NO;   // 전문 일련번호
        private final String STD_TMSG_PRGR_NO = "00";
        private String STN_TMSG_IP;       // 13.109.91.167
        private final String FRS_RQST_SYS_CD = "SIF";
        private String FRS_RQST_DTM;      // YYYYMMDDHHMMSSTTT
        private final String TRMS_SYS_CD = "SIF";
        private final String RQST_RSPS_DV_CD = "S";
        private final String TRSC_SYNC_DV_CD = "S";
        private String TMSG_RQST_DTM;
        private String RECV_SVC_CD;       // 예: HBSSAMCPN0306
        private String INTF_ID;           // 예: SIF00HBSSAMCPN0306
    }

    @Data @Builder
    public static class TransactionHeader {
        private final String STN_MSG_TR_TP_CD = "O";
        private final String SYSTEM_TYPE = "HABIS";
        private String CORP_CD;           // 4000, 8000 등
        private String CMP_NO;            // 법인코드와 동일 설정
        private final String WRKR_NO = "l1711019"; // 가이드 지정 번호
        private final String MASK_AUTH = "0";
    }
}