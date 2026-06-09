package kr.co.winnticket.integration.tosspayments.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Toss Payments 연동 설정 프로퍼티
 * application.yml의 toss.* 항목을 바인딩
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "toss")
public class TossPaymentsProperties {

    /** 클라이언트 키 (FE 위젯에서도 사용 - 노출 가능) */
    private String clientKey;

    /** 시크릿 키 (서버 전용 - 절대 노출 금지) */
    private String secretKey;

    /** 결제 성공 후 리다이렉트 URL */
    private String successUrl;

    /** 결제 실패/취소 후 리다이렉트 URL */
    private String failUrl;
}
