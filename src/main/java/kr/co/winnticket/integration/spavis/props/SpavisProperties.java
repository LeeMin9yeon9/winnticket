package kr.co.winnticket.integration.spavis.props;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix="spavis")
public class SpavisProperties {
    private String baseUrl;
    private String custId;
}
