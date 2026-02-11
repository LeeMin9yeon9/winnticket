package kr.co.winnticket.integration.smartinfini.config;

import kr.co.winnticket.integration.smartinfini.props.SmartInfiniProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(SmartInfiniProperties.class)
public class SmartInfiniConfig {

    @Bean
    public RestTemplate smartInfiniRestTemplate(RestTemplateBuilder builder) {
        // 응답 바디 로깅/재사용을 위해 Buffering
        return builder
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(15))
                .build();
    }

//    @Bean
//    public ObjectMapper smartInfiniObjectMapper() {
//        ObjectMapper om = new ObjectMapper();
//        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        om.findAndRegisterModules();
//        return om;
//    }
}