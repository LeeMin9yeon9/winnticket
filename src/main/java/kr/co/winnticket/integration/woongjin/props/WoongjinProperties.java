package kr.co.winnticket.integration.woongjin.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "woongjin")
public class WoongjinProperties {
    private String baseUrl;
    private String apiToken;
}
