package kr.co.winnticket.integration.payletter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.payletter.config.PayletterHashUtil;
import kr.co.winnticket.integration.payletter.config.PayletterProperties;
import kr.co.winnticket.integration.payletter.dto.*;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
@Log4j2
@Service
@RequiredArgsConstructor
public class PayletterService {

    private final PayletterClient payletterClient;
    private final PayletterProperties properties;
    private final OrderShopMapper orderShopMapper;
    private final ObjectMapper objectMapper;


    // Payletter 결제요청
    @Transactional
    public PayletterPaymentResDto paymentRequest(UUID orderId, String orderNumber, Integer finalPrice, String customerName, String customerEmail,String customerPhone, String pgCode){

        if (orderId == null) throw new IllegalArgumentException("orderId is null");
        if (orderNumber == null || orderNumber.isBlank()) throw new IllegalArgumentException("orderNumber is empty");
        if (finalPrice <= 0) throw new IllegalArgumentException("finalPrice must be > 0");


        //상품명 만들기 (DB에서 첫 상품 + 건수)
        Map<String, Object> summary = orderShopMapper.selectPayletterProductSummary(orderId);

        String firstProductName = summary != null ? (String) summary.get("first_product_name") : null;

        // 주문 내 상품 개수
        Number itemCountNum = summary != null ? (Number) summary.get("item_count") : null;
        int itemCount = (itemCountNum != null) ? itemCountNum.intValue() : 1;

        // 상품명이 없으면 기본 문구 대체
        if (firstProductName == null || firstProductName.isBlank()) {
            firstProductName = "윈앤티켓 주문";
        }

        String productName = (itemCount == 1)
                ? firstProductName
                : firstProductName + " 외 " + (itemCount - 1) + "건";

        // user_id는 "결제자 식별값"이라 전화번호/이메일/세션값 추천
        String userId = null;

        if (customerPhone != null && !customerPhone.isBlank()) {
            userId = customerPhone.replaceAll("[^0-9]", ""); // 숫자만
        } else if (customerEmail != null && !customerEmail.isBlank()) {
            userId = customerEmail;
        } else {
            userId = orderNumber;
        }

        boolean hasEmail = customerEmail != null && !customerEmail.isBlank();

        // payletter 결제 요청dto 생성
        PayletterPaymentReqDto req = PayletterPaymentReqDto.builder()
                .pgCode(pgCode)  // 결제수단
                .userId(userId)       // 주문자ID
                .userName(customerName)  // 주문자이름
                .serviceName(properties.getServiceName())   //결제 서비스명
                .clientId(properties.getClientId())// 가맹점ID
                .orderNo(orderNumber)//  가맹점 주문번호
                .amount(finalPrice)//   결제금액
                .taxfreeAmount(0) // 비과세 금액
                .taxAmount(0)
                .productName(productName)  // 결제상품이름
                .emailFlag(hasEmail ? "Y" : "N")    // 결제내역 메일 수신 여부
                .emailAddr(hasEmail ? customerEmail : null)    // 결제 내역 메일 수신 주소
                .customParameter(orderId.toString()) // 내부 PK 추적용
                .autopayFlag("N")  // 자동결제 여부
                .callbackUrl(properties.getCallbackUrl())  // 결제 성공 결과 URL
                .cancelUrl(properties.getCancelUrl())    // 결제 취소 URL
                .returnUrl(properties.getReturnUrl())    // 결제 완료 후 URL
                .build();


        // payletter  결제 요청 API 호출
        PayletterPaymentResDto res = payletterClient.requestPayment(req);

        if (res == null) {
            throw new IllegalStateException("Payletter 결제요청 실패: 응답 null");
        }

        if (res.getToken() == null) {
            throw new IllegalStateException("Payletter 결제요청 실패 code=" + res.getCode() + ", message=" + res.getMessage());
        }

        // order DB 업데이트 (요청 단계에서는 pg_tid에 token저장)
        orderShopMapper.updatePayletterRequest(
                orderId,
                "PAYLETTER",    // PG사 코드
                String.valueOf(res.getToken()),     // token저장(tid 임시)
                res.getOnlineUrl(),     // PC 결제 URL
                res.getMobileUrl(),      // 모바일 결제 URL
                pgCode
        );

        log.info("[PAYLETTER] paymentRequest success. orderId={}, token={}", orderId, res.getToken());

        return res;
    }

    //callback payload 그대로 저장 + 결제완료 처리
    @Transactional
    public void handleCallback(Map<String, Object> payload) {

        String payloadJson = null;

        try {

            // custom_parameter에서 orderId ㄲㅓㄴㅐㅁ
            String custom = payload.get("custom_parameter") != null ? String.valueOf(payload.get("custom_parameter")) : null;
            if (custom == null || custom.isBlank()) throw new IllegalArgumentException("custom_parameter missing");

            UUID orderId = UUID.fromString(custom);

            // payload(Map) Json 문자열로 저장
            payloadJson = objectMapper.writeValueAsString(payload);

            // 콜백 필수 값 추출
            String userId = payload.get("user_id") != null ? String.valueOf(payload.get("user_id")) : null;
            Integer amount = payload.get("amount") != null ? Integer.parseInt(String.valueOf(payload.get("amount"))) : null;

            String tid = payload.get("tid") != null ? String.valueOf(payload.get("tid")) : null;  // 거래ID
            String cid = payload.get("cid") != null ? String.valueOf(payload.get("cid")) : null;  // 승인번호(문서상 cid)
            String payhash = payload.get("payhash") != null ? String.valueOf(payload.get("payhash")) : null;

            if (userId == null || userId.isBlank()) throw new IllegalArgumentException("user_id missing");
            if (amount == null) throw new IllegalArgumentException("amount missing");
            if (tid == null || tid.isBlank()) throw new IllegalArgumentException("tid missing");
            if (payhash == null || payhash.isBlank()) throw new IllegalArgumentException("payhash missing");

            // 주문 정보 조회
            Map<String, Object> orderInfo = orderShopMapper.selectOrderPaymentInfo(orderId);
            if (orderInfo == null) {
                throw new IllegalStateException("주문 없음 orderId=" + orderId);
            }
            Integer finalPrice = orderInfo.get("final_price") != null
                    ? ((Number) orderInfo.get("final_price")).intValue()
                    : null;

            if (finalPrice == null) throw new IllegalStateException("final_price 없음");

            // 결제금액 불일치 시 실패 처리
            if (amount == null || !amount.equals(finalPrice)) {
                throw new IllegalStateException("결제금액 불일치. order=" + finalPrice + ", callback=" + amount);
            }

            // payhash 검증
            // payhash = SHA256(user_id + amount + tid + 결제요청 API Key)
            String apiKey = properties.getPaymentApiKey();
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalStateException("paymentApiKey 설정 없음");
            }

            String expected = PayletterHashUtil.sha256(userId + amount + tid + apiKey);

            if (!expected.equalsIgnoreCase(payhash)) {
                throw new IllegalStateException("payhash 검증 실패");
            }

            // 결제완료 업데이트
            int updated = orderShopMapper.updatePayletterCallbackSuccessIfNotPaid(orderId, payloadJson, tid, cid);

            // 이미 누군가 먼저 처리했으면 updated=0
            if (updated == 0) {
                log.info("[PAYLETTER] callback already processed. orderId={}", orderId);
                return;
            }

            log.info("[PAYLETTER] callback success. orderId={}, tid={}, cid={}", orderId, tid, cid);

        } catch (Exception e) {
            log.error("[PAYLETTER] callback 처리 실패 payload={}", payloadJson, e);

            // orderId가 파싱되면 FAILED로 기록
            try {
                String custom = payload.get("custom_parameter") != null
                        ? String.valueOf(payload.get("custom_parameter"))
                        : null;

                if (custom != null && !custom.isBlank()) {
                    UUID orderId = UUID.fromString(custom);
                    orderShopMapper.updatePayletterCallbackFailed(orderId, payloadJson, e.getMessage());
                }
            } catch (Exception ignore) {
                // 여기서는 추가 예외 무시
            }

            throw new IllegalStateException("콜백 처리 실패", e);
        }
    }

    // 결제 취소
    @Transactional
    public PayletterCancelResDto cancel(UUID orderId, String ipAddr){

        if (orderId == null) throw new IllegalArgumentException("orderId is null");
        if (ipAddr == null || ipAddr.isBlank()) throw new IllegalArgumentException("ipAddr is null");

        Map<String, Object> orderInfo = orderShopMapper.selectOrderPaymentInfo(orderId);
        if(orderInfo == null) throw new IllegalStateException("주문 없음 orderId="+orderId);

        String pgProvider = orderInfo.get("pg_provider") != null ? String.valueOf(orderInfo.get("pg_provider")) : null;
        if(pgProvider == null || !"PAYLETTER".equalsIgnoreCase(pgProvider)){
            throw new IllegalStateException("PAYLETTER 주문이 아닙니다. pgProvider=" + pgProvider);
        }

        String tid = orderInfo.get("pg_tid") != null ? String.valueOf(orderInfo.get("pg_tid")) : null;
        if (tid == null || tid.isBlank()) throw new IllegalStateException("취소 불가: pg_tid(tid) 없음");


        String phone = orderInfo.get("customer_phone") != null ? String.valueOf(orderInfo.get("customer_phone")) : null;
        String email = orderInfo.get("customer_email") != null ? String.valueOf(orderInfo.get("customer_email")) : null;
        String orderNumber = orderInfo.get("order_number") != null ? String.valueOf(orderInfo.get("order_number")) : null;

        String userId;
        if (phone != null && !phone.isBlank()) userId = phone.replaceAll("[^0-9]", "");
        else if (email != null && !email.isBlank()) userId = email;
        else userId = orderNumber;

        //pgCode
        String pgCode = orderInfo.get("pg_code") != null ? String.valueOf(orderInfo.get("pg_code")) : null;
        if (pgCode == null || pgCode.isBlank()) {
            pgCode = "creditcard";
        }

        PayletterCancelReqDto req = PayletterCancelReqDto.builder()
                .pgCode(pgCode) // creditcard
                .clientId(properties.getClientId())
                .userId(userId)
                .tid(tid)
                .ipAddr(ipAddr)
                .build();

        PayletterCancelResDto res = payletterClient.cancelPayment(req);

        if (res == null) throw new IllegalStateException("[Payletter] 취소 실패: 응답 null");
        if (!res.isCanceled()) {
            throw new IllegalStateException("[Payletter] 취소 실패 code=" + res.getCode() + ", message=" + res.getMessage());
        }
        // DB 업데이트
        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(res);
        } catch (Exception e) {
            payloadJson = null;
        }

        orderShopMapper.updatePayletterCancelSuccess(orderId, payloadJson);

        log.info("[PAYLETTER] cancel success orderId={}, tid={}", orderId, tid);
        return res;

    }

    // 결제내역 조회
    public PayletterTransactionListResDto getTransactionList(String date, String dateType, String pgCode, String orderNumber) {

        // date 기본값 - 오늘
        if (date == null || date.isBlank()) {
            date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        // dateType 기본값 - transaction
        if (dateType == null || dateType.isBlank()) {
            dateType = "transaction";
        }

        // pgCode 기본값: creditcard
        if (pgCode == null || pgCode.isBlank()) {
            pgCode = "creditcard";
        }

        PayletterTransactionListResDto res = payletterClient.getTransactionList(
                properties.getClientId(),
                date,
                dateType,
                pgCode,
                orderNumber
        );

        if (res == null) {
            throw new IllegalStateException("[PAYLETTER] transaction/list 응답 null");
        }

        // 페이레터 응답이 실패일 때 방지
        if (res.getCode() != null && res.getCode() != 0) {
            throw new IllegalStateException("[PAYLETTER] transaction/list 실패 code=" + res.getCode()
                    + ", message=" + res.getMessage());
        }

        return res;
    }

    //거래상태조회
    public PayletterPaymentStatusResDto getPaymentStatus(String orderNumber) {

        if (orderNumber == null || orderNumber.isBlank()) {
            throw new IllegalArgumentException("orderNo is empty");
        }

        PayletterPaymentStatusReqDto req = PayletterPaymentStatusReqDto.builder()
                .clientId(properties.getClientId())
                .orderNo(orderNumber) // Payletter는 order_no로 받음
                .build();

        PayletterPaymentStatusResDto res = payletterClient.getPaymentStatus(req);

        if (res == null) {
            throw new IllegalStateException("[PAYLETTER] payments/status 응답 null");
        }

        if (res.getCode() != null && res.getCode() != 0) {
            throw new IllegalStateException("[PAYLETTER] payments/status 실패 code=" + res.getCode()
                    + ", message=" + res.getMessage());
        }

        return res;
    }
}
