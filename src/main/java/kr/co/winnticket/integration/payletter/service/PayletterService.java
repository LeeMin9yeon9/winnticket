package kr.co.winnticket.integration.payletter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.payletter.config.PayletterProperties;
import kr.co.winnticket.integration.payletter.dto.PayletterPaymentReqDto;
import kr.co.winnticket.integration.payletter.dto.PayletterPaymentResDto;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PayletterPaymentResDto paymentRequest(UUID orderId, String orderNumber, Integer finalPrice, String customerName, String customerEmail,String customerPhone){

        if (orderId == null) throw new IllegalArgumentException("orderId is null");
        if (orderNumber == null || orderNumber.isBlank()) throw new IllegalArgumentException("orderNumber is empty");
        if (finalPrice <= 0) throw new IllegalArgumentException("finalPrice must be > 0");


        //상품명 만들기 (DB에서 첫 상품 + 건수)
        Map<String, Object> summary = orderShopMapper.selectPayletterProductSummary(orderId);

        String firstProductName = summary != null ? (String) summary.get("first_product_name") : null;
        Number itemCountNum = summary != null ? (Number) summary.get("item_count") : null;
        int itemCount = (itemCountNum != null) ? itemCountNum.intValue() : 1;

        if (firstProductName == null || firstProductName.isBlank()) {
            firstProductName = "윈앤티켓 주문";
        }

        String productName = (itemCount == 1)
                ? firstProductName
                : firstProductName + " 외 " + (itemCount - 1) + "건";

        // user_id는 "결제자 식별값"이라 전화번호/이메일/세션값 추천
        String userId = (customerPhone != null && !customerPhone.isBlank())
                ? customerPhone
                : (customerEmail != null ? customerEmail : orderNumber);

        boolean hasEmail = customerEmail != null && !customerEmail.isBlank();

        // 주문조회
        PayletterPaymentReqDto req = PayletterPaymentReqDto.builder()
                .pgCode("creditcard")  // 결제수단
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

        log.info("[PAYLETTER] paymentRequest start. orderId={}, orderNo={}, amount={}",
                orderId, orderNumber, finalPrice);

        // payletter API 호출
        PayletterPaymentResDto res = payletterClient.requestPayment(req);

        if (res == null) {
            throw new IllegalStateException("Payletter 결제요청 실패: 응답 null");
        }

        if (res.getToken() == null) {
            throw new IllegalStateException("Payletter 결제요청 실패 code=" + res.getCode() + ", message=" + res.getMessage());
        }

        // orders 테이블 업데이트 (요청 단계에서는 pg_tid에 token저장)
        orderShopMapper.updatePayletterRequest(
                orderId,
                "PAYLETTER",
                String.valueOf(res.getToken()),
                res.getOnlineUrl(),
                res.getMobileUrl()
        );

        log.info("[PAYLETTER] paymentRequest success. orderId={}, token={}", orderId, res.getToken());

        return res;
    }

    //callback payload 그대로 저장 + 결제완료 처리
    @Transactional
    public void handleCallback(Map<String, Object> payload) {
        try {
            String custom = payload.get("custom_parameter") != null ? String.valueOf(payload.get("custom_parameter")) : null;
            if (custom == null || custom.isBlank()) throw new IllegalArgumentException("custom_parameter missing");

            UUID orderId = UUID.fromString(custom);

            String payloadJson = objectMapper.writeValueAsString(payload);

            orderShopMapper.updatePayletterCallbackSuccess(orderId, payloadJson);

        } catch (Exception e) {
            throw new IllegalStateException("콜백 처리 실패", e);
        }
    }
}
