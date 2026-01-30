package kr.co.winnticket.integration.mair.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "mair")
public class MairProperties {

    private String mode; // dev | prod

    private String mkno;  // 채널 구분자 HDGH
    private String mkid;  // 채널 아이디 HDGH1

    private Integer connectTimeoutMs = 2000;   // 연결 시도 시간 제한
    private Integer responseTimeoutMs = 5000;   // 응답 대기 시간 제한

    private String issueUrl;  // 쿠폰 발송 API 주소
    private String cancelUrl; // 쿠폰 취소 API 주소
    private String useCheckUrl; // 쿠폰 사용여부 조회 API 주소
}
