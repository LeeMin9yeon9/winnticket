package kr.co.winnticket.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String port;

    @Bean
    public OpenAPI openAPI(Environment environment) {

        String[] profiles = environment.getActiveProfiles();
        String activeProfile = (profiles.length > 0) ? profiles[0] : "local";

        // profile 별 서버 주소 설정
        String serverUrl;
        String description;

        switch (activeProfile) {
            case "dev":
                serverUrl = "https://api.winnticket.store";
                description = "DEV Server";
                break;
            default:
                serverUrl = "http://localhost:" + port;
                description = "LOCAL Server";
        }

        return new OpenAPI()
                .info(new Info()
                        .title("WinnTicket API Docs")
                        .description("윈앤티켓 백엔드 API 명세서")
                        .version("v1.0"))
                .servers(List.of(
                        new Server().url(serverUrl).description(description)
                ));
    }
}