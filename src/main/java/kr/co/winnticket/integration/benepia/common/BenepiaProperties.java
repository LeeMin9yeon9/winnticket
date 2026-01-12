package kr.co.winnticket.integration.benepia.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix="benepia")
@Schema(title = "[베네피아 공통 설정] BenepiaProperties")
public class BenepiaProperties {
    //seed key
    private String seedKey;

    // 고객사 코드
    private String custCoCd;

    // SSO 확인 API URL
    private String confirmUrl;

    // 토큰 생성 APU URL
    private String tokenCreateUrl;
}
