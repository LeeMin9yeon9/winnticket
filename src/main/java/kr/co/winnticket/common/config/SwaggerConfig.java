package kr.co.winnticket.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                    .title("윈티켓 API 문서")                          // 문서 제목
                    .version("v1.0.0")                               // 버전
                    .description("윈티켓 프로젝트의 API 명세 문서입니다."));  // 설명
    }
}
