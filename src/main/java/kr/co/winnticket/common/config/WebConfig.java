package kr.co.winnticket.common.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * CORS 설정
 * 프론트엔드(winnticket.store)에서 API(api.winnticket.store)를 호출할 수 있도록 허용
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        // 로컬 개발 환경
                        "http://localhost:5173",
                        "http://localhost:3000",
                        "http://localhost:4173",
                        "https://winnticket.store",
                        "https://www.winnticket.store"
                        // IP 직접 접근은 굳이 필요 없음. 쓰고 싶다면:
                        // "http://43.201.23.43",
                        // "http://43.201.23.43:8080"
                )
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // 1시간 동안 preflight 요청 캐시
    }
}
