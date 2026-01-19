package kr.co.winnticket.integration.payletter.service;

import kr.co.winnticket.integration.payletter.config.PayletterProperties;
import kr.co.winnticket.integration.payletter.dto.PayletterPaymentReqDto;
import kr.co.winnticket.integration.payletter.dto.PayletterPaymentResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Log4j2
@Component
@RequiredArgsConstructor
public class PayletterClient {

    private final PayletterProperties props;

    // payletter 결제 요청 API 호출 HTTP클라이언트
    public PayletterPaymentResDto requestPayment(PayletterPaymentReqDto request){

        String apiKey = props.getPaymentApiKey();

        log.info("[PAYLETTER] baseUrl={}", props.getBaseUrl());
        log.info("[PAYLETTER] clientId={}", props.getClientId());
        log.info("[PAYLETTER] apiKey length={}", apiKey == null ? "null" : apiKey.length());
        try{

        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                // 헤더 인증키
                .defaultHeader(HttpHeaders.AUTHORIZATION, "PLKEY " + props.getPaymentApiKey())

                .build()
                // post요청수행
                .post()
                .uri("v1.0/payments/request")
                .bodyValue(request)
                // 응답처리
                .retrieve()
                .bodyToMono(PayletterPaymentResDto.class)
                .block();
    }catch (WebClientResponseException e){
            PayletterPaymentResDto fail = new PayletterPaymentResDto();
            fail.setCode(e.getStatusCode().value());
            fail.setMessage(e.getResponseBodyAsString());
            return fail;
        }
    }
}
