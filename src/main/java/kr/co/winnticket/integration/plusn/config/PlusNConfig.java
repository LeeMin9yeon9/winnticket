package kr.co.winnticket.integration.plusn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class PlusNConfig {

    @Bean
    public RestTemplate plusNRestTemplate() {

        RestTemplate restTemplate = new RestTemplate();

        // Jackson 컨버터가 text/html 도 JSON으로 읽게 설정
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter();

        converter.setSupportedMediaTypes(
                List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML)
        );

        restTemplate.getMessageConverters().add(0, converter);

        return restTemplate;
    }
}
