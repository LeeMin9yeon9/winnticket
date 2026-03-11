package kr.co.winnticket.integration.aquaplanet.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AquaPlanetConfig {

    @Bean
    public RestTemplate aquaPlanetRestTemplate() {
        return new RestTemplate();
    }

}