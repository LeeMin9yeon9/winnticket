package kr.co.winnticket.integration.payletter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.payletter.config.PayletterProperties;
import kr.co.winnticket.integration.payletter.dto.PayletterCancelReqDto;
import kr.co.winnticket.integration.payletter.dto.PayletterCancelResDto;
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
    private final ObjectMapper objectMapper;

    // payletter 결제 요청 API 호출 HTTP클라이언트
    public PayletterPaymentResDto requestPayment(PayletterPaymentReqDto request){

        String apiKey = props.getPaymentApiKey();

        log.info("[PAYLETTER] baseUrl={}", props.getBaseUrl());
        log.info("[PAYLETTER] clientId={}", props.getClientId());
        log.info("[PAYLETTER] apiKey length={}", apiKey == null ? "null" : apiKey.length());
        log.info("[PAYLETTER] pgCode={}", request.getPgCode());
        try{

            log.info("[PAYLETTER] requestJson={}", objectMapper.writeValueAsString(request));
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
        }catch (Exception e) {
            throw new IllegalStateException("[Paylette] requestPayment error", e);
        }
    }


    // payletter 취소 요청
    public PayletterCancelResDto cancelPayment(PayletterCancelReqDto reqDto){

        String apiKey = props.getPaymentApiKey();

        log.info("[PAYLETTER] baseUrl={}", props.getBaseUrl());
        log.info("[PAYLETTER] clientId={}", props.getClientId());
        log.info("[PAYLETTER] apiKey length={}", apiKey == null ? "null" : apiKey.length());
        log.info("[PAYLETTER] cancel pgcode={}", reqDto.getPgCode());
        log.info("[PAYLETTER] cancel tid={}", reqDto.getTid());

        try{
            log.info("[PAYLETTER] cancel requsetJson={}",objectMapper.writeValueAsString(reqDto));

            return WebClient.builder()
                    .baseUrl((props.getBaseUrl()))
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "PLKEY "+apiKey)
                    .build()
                    .post()
                    .uri("v1.0/payments/cancel")
                    .bodyValue(reqDto)
                    .retrieve()
                    .bodyToMono(PayletterCancelResDto.class)
                    .block();
        } catch (WebClientResponseException e){
            PayletterCancelResDto fail = new PayletterCancelResDto();
            fail.setCode(e.getStatusCode().value());
            fail.setMessage(e.getResponseBodyAsString());

            return fail;

        } catch (Exception e) {
            throw new IllegalStateException("[Payletter] cancelPayment error", e);
        }
    }
}
