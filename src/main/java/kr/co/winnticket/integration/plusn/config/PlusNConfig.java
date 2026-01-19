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

        // Jackson 컨버터 생성
        MappingJackson2HttpMessageConverter jacksonConverter =
                new MappingJackson2HttpMessageConverter();

        // text/html 도 JSON으로 파싱하도록 추가
        List<MediaType> mediaTypes = new ArrayList<>(jacksonConverter.getSupportedMediaTypes());
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.TEXT_PLAIN);
        jacksonConverter.setSupportedMediaTypes(mediaTypes);

        // 기존 컨버터 앞에 삽입
        List<HttpMessageConverter<?>> converters =
                new ArrayList<>(restTemplate.getMessageConverters());
        converters.add(0, jacksonConverter);

        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }
}
