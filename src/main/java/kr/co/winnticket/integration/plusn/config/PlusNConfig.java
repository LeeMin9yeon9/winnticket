package kr.co.winnticket.integration.plusn.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PlusNConfig {

    @Bean
    public RestTemplate plusNRestTemplate() {
        return new RestTemplate();
    }
}
