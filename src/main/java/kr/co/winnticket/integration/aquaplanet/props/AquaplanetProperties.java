package kr.co.winnticket.integration.aquaplanet.props;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aquaplanet")
@Getter
@Setter
public class AquaplanetProperties {

    private String baseUrl;

    // SystemHeader
    private String tmsgVerDvCd;
    private String envrInfoDvCd;
    private String stnMsgEncpCd;
    private String stnMsgCompCd;
    private String langCd;
    private String stdTmsgPrgrNo;
    private String frsRqstSysCd;
    private String rqstRspsDvCd;
    private String trscSyncDvCd;

    // TransactionHeader
    private String stnMsgTrTpCd;
    private String systemType;
    private String wrkrNo;
    private String maskAuth;
}
