package kr.co.winnticket.integration.coreworks.config;

import kr.co.winnticket.integration.coreworks.props.CoreWorksProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(CoreWorksProperties.class)
public class CoreWorksConfig {

    @Bean
    public RestTemplate coreWorksRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }
}
