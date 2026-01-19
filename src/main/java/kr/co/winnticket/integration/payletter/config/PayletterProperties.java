package kr.co.winnticket.integration.payletter.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "payletter")
@Schema(name = "PayletterProperties", description = "Payletter 연동 설정 프로퍼티")
public class PayletterProperties {

    @Schema(description = "Payletter API Base URL", example = "테스트 : https://testpgapi.payletter.com")
    private String baseUrl;

    @Schema(description = "가맹점ID", example = "pay_test")
    private String clientId;

    @Schema(description = "Payletter 결제 API Key 노출금지", example = "MTFBNTAzNTEwNDAxQUIyMjlCQzgwNTg1MkU4MkZENDA=")
    private String paymentApiKey;

    @Schema(description = "서비스명" , example = "페이레터")
    private String serviceName;

    @Schema(description = "결제 완료 후 리턴 URL (사용자 브라우저 이동)", example = "https://winnticket.store/payletter/return")
    private String returnUrl;

    @Schema(description = "결제 결과 콜백 URL (서버 to 서버 통지)", example = "https://api.winnticket.store/payletter/callback")
    private String callbackUrl;

    @Schema(description = "결제 취소 URL", example = "https://winnticket.store/payletter/cancel")
    private String cancelUrl;
}
