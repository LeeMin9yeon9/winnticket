package kr.co.winnticket.integration.playstory.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "playstory")
public class PlaystoryConfig {
    private String baseUrl;
    private String chnId;
}

