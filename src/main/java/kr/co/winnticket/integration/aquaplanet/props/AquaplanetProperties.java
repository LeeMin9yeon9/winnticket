package kr.co.winnticket.integration.aquaplanet.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "integration.aquaplanet")
public class AquaplanetProperties {

    private String baseUrl;

    private String tmsgVerDvCd;       // "01"
    private String envrInfoDvCd;      // "D" or "R"
    private String langCd;            // "KO"
    private String stnMsgEncpCd;      // "0"
    private String stnMsgCompCd;      // "0"
    private String stdTmsgPrgrNo;     // "00"
    private String frsRqstSysCd;      // "SIF"
    private String trscSyncDvCd;      // "S"
    private String rqstRspsDvCd;      // "S"

    private String stnMsgTrTpCd;      // "O"
    private String systemType;        // "HABIS"
    private String maskAuth;          // "0"

    private String wrkrNo;

    private String recvSvcCdContract;
    private String intfIdContract;

    private String recvSvcCdIssue;
    private String intfIdIssue;

    private String recvSvcCdCancel;
    private String intfIdCancel;

    private String recvSvcCdUseHistory;
    private String intfIdUseHistory;

    private String recvSvcCdUseDailyHistory;
    private String intfIdUseDailyHistory;
}
