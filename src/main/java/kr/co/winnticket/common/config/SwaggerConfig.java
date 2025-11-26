package kr.co.winnticket.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
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

        String activeProfile = environment.getActiveProfiles().length > 0
                ? environment.getActiveProfiles()[0]
                : "local";

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
                break;
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

    /* ================= GROUP API SETTING ================= */

    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
                .group("0000. 공통")
                .packagesToScan("kr.co.winnticket.status", "kr.co.winnticket.common")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi communityApi() {
        return GroupedOpenApi.builder()
                .group("0100. 커뮤니티")
                .packagesToScan("kr.co.winnticket.community")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi menuApi() {
        return GroupedOpenApi.builder()
                .group("0200. 메뉴")
                .packagesToScan("kr.co.winnticket.menu")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi productApi() {
        return GroupedOpenApi.builder()
                .group("0300. 상품")
                .packagesToScan("kr.co.winnticket.product")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("0400. 주문")
                .packagesToScan("kr.co.winnticket.order")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("0500. 인증/로그인")
                .packagesToScan("kr.co.winnticket.auth")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi siteinfoApi() {
        return GroupedOpenApi.builder()
                .group("0600. 사이트관리")
                .packagesToScan("kr.co.winnticket.siteinfo")
                .pathsToMatch("/api/**")
                .build();
    }

}
