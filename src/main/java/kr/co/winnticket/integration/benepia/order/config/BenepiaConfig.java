package kr.co.winnticket.integration.benepia.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class BenepiaConfig {

    @Bean
    public RestTemplate benepiaRestTemplate(
            RestTemplateBuilder builder,
            @Value("${benepia.connect-timeout-ms:5000}") long connectTimeoutMs,
            @Value("${benepia.read-timeout-ms:10000}") long readTimeoutMs
    ) {
        RestTemplate restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(connectTimeoutMs))
                .setReadTimeout(Duration.ofMillis(readTimeoutMs))
                .build();

        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().add("Accept", "application/json");
            return execution.execute(request, body);
        };
        restTemplate.getInterceptors().add(interceptor);

        return restTemplate;
    }
}