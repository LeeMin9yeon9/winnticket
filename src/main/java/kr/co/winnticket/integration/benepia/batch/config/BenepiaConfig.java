package kr.co.winnticket.integration.benepia.batch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BenepiaConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}