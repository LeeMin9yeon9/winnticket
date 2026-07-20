package kr.co.winnticket.integration.tosspayments.service;

import kr.co.winnticket.integration.tosspayments.config.TossPaymentsProperties;
import kr.co.winnticket.integration.tosspayments.dto.TossCancelReqDto;
import kr.co.winnticket.integration.tosspayments.dto.TossPaymentResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Base64;
import java.util.Map;

/**
 * Toss Payments REST API 클라이언트
 * 결제 승인 및 취소 API 호출 담당
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class TossPaymentsClient {

    private static final String TOSS_BASE_URL = "https://api.tosspayments.com";

    private final TossPaymentsProperties props;

    /**
     * Basic Auth 헤더 생성
     * Toss Payments 인증 방식: Base64(secretKey + ":")
     */
    private String basicAuthHeader() {
        String encoded = Base64.getEncoder().encodeToString(
                (props.getSecretKey() + ":").getBytes()
        );
        return "Basic " + encoded;
    }

    /**
     * 결제 승인 API
     * FE가 successUrl에서 받은 paymentKey, orderId, amount를 Toss에 전달하여 최종 승인
     * POST https://api.tosspayments.com/v1/payments/confirm
     */
    public TossPaymentResDto confirm(String paymentKey, String orderId, Integer amount) {
        try {
            Map<String, Object> body = Map.of(
                    "paymentKey", paymentKey,
                    "orderId", orderId,
                    "amount", amount
            );

            log.info("[TOSS] confirm 요청 paymentKey={}, orderId={}, amount={}", paymentKey, orderId, amount);

            TossPaymentResDto result = WebClient.builder()
                    .baseUrl(TOSS_BASE_URL)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader())
                    .build()
                    .post()
                    .uri("/v1/payments/confirm")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(TossPaymentResDto.class)
                    .block();

            log.info("[TOSS] confirm 응답 status={}", result != null ? result.getStatus() : "null");
            return result;

        } catch (WebClientResponseException e) {
            log.error("[TOSS] confirm 실패 httpStatus={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            TossPaymentResDto fail = new TossPaymentResDto();
            fail.setCode(e.getStatusCode().value());
            fail.setMessage(e.getResponseBodyAsString());
            return fail;
        } catch (Exception e) {
            throw new IllegalStateException("[TOSS] confirm 오류", e);
        }
    }

    /**
     * 결제 취소 API (부분취소 포함)
     * cancelAmount가 null이면 전액 취소
     * POST https://api.tosspayments.com/v1/payments/{paymentKey}/cancel
     */
    public TossPaymentResDto cancel(String paymentKey, String cancelReason, Integer cancelAmount) {
        try {
            TossCancelReqDto req = TossCancelReqDto.builder()
                    .cancelReason(cancelReason)
                    .cancelAmount(cancelAmount)
                    .taxFreeAmount(0)
                    .build();

            log.info("[TOSS] cancel 요청 paymentKey={}, reason={}, amount={}", paymentKey, cancelReason, cancelAmount);

            TossPaymentResDto result = WebClient.builder()
                    .baseUrl(TOSS_BASE_URL)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader())
                    .build()
                    .post()
                    .uri("/v1/payments/{paymentKey}/cancel", paymentKey)
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(TossPaymentResDto.class)
                    .block();

            log.info("[TOSS] cancel 응답 status={}", result != null ? result.getStatus() : "null");
            return result;

        } catch (WebClientResponseException e) {
            log.error("[TOSS] cancel 실패 httpStatus={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            TossPaymentResDto fail = new TossPaymentResDto();
            fail.setCode(e.getStatusCode().value());
            fail.setMessage(e.getResponseBodyAsString());
            return fail;
        } catch (Exception e) {
            throw new IllegalStateException("[TOSS] cancel 오류", e);
        }
    }
}
