package kr.co.winnticket.integration.spavis.config;

import kr.co.winnticket.integration.spavis.props.SpavisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SpavisConfig {

    @Bean
    public RestTemplate spavisRestTemplate() {
        return new RestTemplate();
    }
}
