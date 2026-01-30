package kr.co.winnticket.integration.mair.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.mair.dto.MairCouponResDto;
import kr.co.winnticket.integration.mair.props.MairProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Log4j2
public class MairCouponClient {

    private final MairProperties properties;
    private final WebClient mairWebClient;
    private final ObjectMapper objectMapper;

    // 쿠폰 발송 요청
    public MairCouponResDto issue(String itcd, String trno, String odnm, String odhp) {
        MultiValueMap<String, String> form = baseForm(itcd, trno); // 상품코드 , 거래고유번호
        form.add("ODNM", odnm);  // 회원명
        form.add("ODHP", odhp);  // 발송핸드폰번호

        return postForm(properties.getIssueUrl(), form);
    }

    // 쿠폰 취소 요청
    public MairCouponResDto cancel(String itcd, String trno) {
        MultiValueMap<String, String> form = baseForm(itcd, trno);
        return postForm(properties.getCancelUrl(), form);
    }

    // 사용여부 확인
    public MairCouponResDto useCheck(String itcd, String trno) {
        MultiValueMap<String, String> form = baseForm(itcd, trno);
        return postForm(properties.getUseCheckUrl(), form);
    }


    // 요청 파라미터 form 방식(공통)
    private MultiValueMap<String, String> baseForm(String itcd, String trno) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("MKNO", properties.getMkno()); // 채널구분코드
        form.add("MKID", properties.getMkid());  // 채널 아이디
        form.add("ITCD", itcd);   // 상품코드
        form.add("TRNO", trno);   // 거래고유번호
        return form;
    }

    // POST 요청 보내기
    private MairCouponResDto postForm(String url, MultiValueMap<String, String> form) {

        try {
            String raw = mairWebClient
                    .post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.ALL)
                    .body(BodyInserters.fromFormData(form))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("[MAIR] url={}, raw={}", url, raw);

            if (raw == null || raw.isBlank()) {
                throw new IllegalStateException("MAIR response is empty");
            }

            // ✅ JSONP 형태면 callback 제거
            String json = raw.trim();
            if (json.contains("(") && json.endsWith(")")) {
                json = json.replaceAll("^[^(]*\\(", "").replaceAll("\\);?$", "");
            }

            return objectMapper.readValue(json, MairCouponResDto.class);

        } catch (WebClientResponseException e) {
            log.error("[MAIR] HTTP ERROR status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            log.error("[MAIR] PARSE ERROR url={}, form={}, msg={}", url, form, e.getMessage(), e);
            throw new RuntimeException("MAIR response parse failed", e);
        }
    }

    }
