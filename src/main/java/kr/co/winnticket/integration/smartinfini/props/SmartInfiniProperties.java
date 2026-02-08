package kr.co.winnticket.integration.smartinfini.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "smartinfini")
public class SmartInfiniProperties {

    private String baseUrl;
    private String token;
    private String channelId;
}