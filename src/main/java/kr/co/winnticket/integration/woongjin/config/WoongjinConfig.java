package kr.co.winnticket.integration.woongjin.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WoongjinConfig {

    @Bean
    public RestTemplate woongjinRestTemplate() {
        return new RestTemplate();
    }
}
