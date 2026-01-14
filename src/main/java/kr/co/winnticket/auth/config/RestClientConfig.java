package kr.co.winnticket.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient benepiaRestClient(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(10_000);

        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
}
