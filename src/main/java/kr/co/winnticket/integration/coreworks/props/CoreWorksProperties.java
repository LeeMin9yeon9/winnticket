package kr.co.winnticket.integration.coreworks.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix = "coreworks")
public class CoreWorksProperties {
    private String baseUrl;
    private String authToken;
    private String channelCd;
}