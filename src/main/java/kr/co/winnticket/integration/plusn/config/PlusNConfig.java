package kr.co.winnticket.integration.plusn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class PlusNConfig {

    @Bean
    public RestTemplate plusNRestTemplate() {

        RestTemplate restTemplate = new RestTemplate();

        // 1) String 컨버터 (text/html 읽기용)
        StringHttpMessageConverter stringConverter =
                new StringHttpMessageConverter(StandardCharsets.UTF_8);

        stringConverter.setSupportedMediaTypes(
                List.of(MediaType.TEXT_HTML, MediaType.TEXT_PLAIN)
        );

        // 2) Jackson 컨버터 (JSON 파싱용)
        MappingJackson2HttpMessageConverter jsonConverter =
                new MappingJackson2HttpMessageConverter();

        jsonConverter.setSupportedMediaTypes(
                List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_HTML)
        );

        // 3) 컨버터 우선순위 등록
        restTemplate.getMessageConverters().clear();
        restTemplate.getMessageConverters().add(stringConverter);
        restTemplate.getMessageConverters().add(jsonConverter);

        return restTemplate;
    }
}
