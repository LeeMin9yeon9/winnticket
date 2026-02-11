package kr.co.winnticket.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "WinnTicket API Docs", version = "v1"),
        security = { @SecurityRequirement(name = "bearerAuth") }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
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
               // serverUrl = "https://api.winnticket.store";
                description = "DEV Server";
                break;
            default:
                serverUrl = "http://localhost:" + port;
                description = "LOCAL Server";
                break;
        }

        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("WinnTicket API Docs")
                        .description("윈앤티켓 백엔드 API 명세서")
                        .version("v1.0"))
                .servers(List.of(
                        new io.swagger.v3.oas.models.servers.Server()
                                .url("/")   // 🔥 핵심
                                .description("Current Server")
                ));

//                .info(new io.swagger.v3.oas.models.info.Info()
//                        .title("WinnTicket API Docs")
//                        .description("윈앤티켓 백엔드 API 명세서")
//                        .version("v1.0"));
//                // .servers(List.of(
//                //         new Server().url(serverUrl).description(description)
//                // ));
    }

    /* ================= GROUP API SETTING ================= */

    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
                .group("공통")
                .packagesToScan("kr.co.winnticket.status", "kr.co.winnticket.common")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi communityApi() {
        return GroupedOpenApi.builder()
                .group("커뮤니티")
                .packagesToScan("kr.co.winnticket.community")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi menuApi() {
        return GroupedOpenApi.builder()
                .group("메뉴")
                .packagesToScan("kr.co.winnticket.menu")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi productApi() {
        return GroupedOpenApi.builder()
                .group("상품")
                .packagesToScan("kr.co.winnticket.product")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("주문")
                .packagesToScan("kr.co.winnticket.order")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("인증 및 로그인")
                .packagesToScan("kr.co.winnticket.auth")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi siteinfoApi() {
        return GroupedOpenApi.builder()
                .group("사이트관리")
                .packagesToScan("kr.co.winnticket.siteinfo")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi partnersApi() {
        return GroupedOpenApi.builder()
                .group("파트너관리")
                .packagesToScan("kr.co.winnticket.partners")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi channelsApi() {
        return GroupedOpenApi.builder()
                .group("채널관리")
                .packagesToScan("kr.co.winnticket.channels")
                .pathsToMatch("/api/**")
                .build();
    }
        
    @Bean
    public GroupedOpenApi popupApi() {
        return GroupedOpenApi.builder()
                .group("팝업관리")
                .packagesToScan("kr.co.winnticket.popup")
                .pathsToMatch("/api/**")
                .build();
    }
       
    @Bean
    public GroupedOpenApi bannerApi() {
        return GroupedOpenApi.builder()
                .group("배너관리")
                .packagesToScan("kr.co.winnticket.banner")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi cartApi() {
        return GroupedOpenApi.builder()
                .group("장바구니")
                .packagesToScan("kr.co.winnticket.cart")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi intergrationApi() {
        return GroupedOpenApi.builder()
                .group("api연동테스트")
                .packagesToScan("kr.co.winnticket.integration")
                .pathsToMatch("/api/**")
                .build();
    }

}
