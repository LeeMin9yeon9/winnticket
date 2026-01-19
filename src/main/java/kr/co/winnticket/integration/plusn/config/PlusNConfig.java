package kr.co.winnticket.integration.plusn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

        // 기존 컨버터 목록 가져오기
        List<HttpMessageConverter<?>> converters =
                new ArrayList<>(restTemplate.getMessageConverters());

        // Jackson JSON 컨버터를 맨 앞에 추가 (우선 적용)
        converters.add(0, new MappingJackson2HttpMessageConverter());

        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }
}
