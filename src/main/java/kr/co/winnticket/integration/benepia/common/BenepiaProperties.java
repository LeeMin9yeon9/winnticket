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

    private String seedKey;
}
