package kr.co.winnticket.integration.lscompany.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "lscompany")
public class LsCompanyProperties {
    private String baseUrl;
    private String agentNo;
    private String token;

}
