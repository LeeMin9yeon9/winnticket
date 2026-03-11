package kr.co.winnticket.integration.aquaplanet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "integration.aquaplanet")
public class AquaPlanetProperties {

    private String baseUrl;
    private String corpCd;
    private String contNo;
    private String wrkrNo;
    private String systemType;
    private String maskAuth;
    private String systemName;
    private String stnTmsgIp;
    private String stnTmsgMac;
    private String envrInfoDvCd;
}