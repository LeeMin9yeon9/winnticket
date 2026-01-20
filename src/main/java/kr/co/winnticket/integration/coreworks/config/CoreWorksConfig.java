package kr.co.winnticket.integration.coreworks.config;

import kr.co.winnticket.integration.coreworks.props.CoreWorksProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(CoreWorksProperties.class)
public class CoreWorksConfig {

    @Bean
    public RestTemplate coreWorksRestTemplate() {
        return new RestTemplate();
    }
}
