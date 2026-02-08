package kr.co.winnticket.integration.smartinfini.config;

import kr.co.winnticket.integration.smartinfini.props.SmartInfiniProperties;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "smartinfini")
public class SmartInfiniConfig {

    private String baseUrl;
    private String token;
}