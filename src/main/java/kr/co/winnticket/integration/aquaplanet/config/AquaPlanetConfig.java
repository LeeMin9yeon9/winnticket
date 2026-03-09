package kr.co.winnticket.integration.aquaplanet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import kr.co.winnticket.integration.aquaplanet.props.AquaplanetProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Configuration
public class AquaPlanetConfig {

    @Bean
    public RestTemplate aquaPlanetRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();

        // 어떤 Content-Type이 오더라도 JSON으로 변환 시도
        jsonConverter.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_JSON,
                MediaType.TEXT_PLAIN,
                MediaType.TEXT_HTML,
                new MediaType("application", "json", StandardCharsets.UTF_8)
        ));

        restTemplate.getMessageConverters().add(0, jsonConverter);
        return restTemplate;
    }
}
