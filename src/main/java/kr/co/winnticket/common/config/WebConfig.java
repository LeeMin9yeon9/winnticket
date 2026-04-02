package kr.co.winnticket.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * CORS 설정
 * 프론트엔드(winnticket.store)에서 API(api.winnticket.store)를 호출할 수 있도록 허용
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:}")
    private String uploadDir;

    /**
     * 🔹 Reverse Proxy(nginx) 환경에서
     *    X-Forwarded-Proto / Host 헤더를 읽도록 강제하는 필터
     *    → swagger가 HTTP/내부IP로 URL 생성하는 문제 해결
     */
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    /**
     * 로컬/개발 환경에서 업로드된 파일을 /uploads/** 경로로 서빙
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (uploadDir != null && !uploadDir.isBlank()) {
            String location = uploadDir.endsWith("/") ? "file:" + uploadDir : "file:" + uploadDir + "/";
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(location);
        }
    }
}

