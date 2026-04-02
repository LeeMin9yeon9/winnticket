package kr.co.winnticket.integration.payletter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.common.util.ClientIpProvider;
import kr.co.winnticket.integration.payletter.config.PayletterHashUtil;
import kr.co.winnticket.integration.payletter.config.PayletterProperties;
import kr.co.winnticket.integration.payletter.dto.*;
import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Log4j2
@Service
@RequiredArgsConstructor
public class PayletterService {

    private final PayletterClient payletterClient;
    private final PayletterProperties properties;
    private final OrderShopMapper orderShopMapper;
    private final OrderMapper orderAdminMapper;
    private final ObjectMapper objectMapper;
    private final ClientIpProvider clientIpProvider;


    // Payletter 결제요청
    @Transactional
    public PayletterPaymentResDto paymentRequest(UUID orderId, String orderNumber, Integer finalPrice, String customerName, String customerEmail,String customerPhone, String paymentMethod){

        if (orderId == null) throw new IllegalArgumentException("orderId is null");
        if (orderNumber == null || orderNumber.isBlank()) throw new IllegalArgumentException("orderNumber is empty");
        if (finalPrice <= 0) throw new IllegalArgumentException("finalPrice must be > 0");

        String pgCode = kr.co.winnticket.integration.payletter.config.PayletterMapper.toPayletterPgCode(paymentMethod);

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

        try {
            if(payload == null || payload.isEmpty()){
                log.warn("[PAYLETTER] empty callback");
                return;
            }

            // custom_parameter에서 orderNumber 사용
            String orderIdStr = payload.get("custom_parameter") != null ? String.valueOf(payload.get("custom_parameter")) : null;

            if(orderIdStr == null || orderIdStr.isBlank()){
                throw new IllegalStateException("custom_parameter 없음");
            }

            UUID orderId = UUID.fromString(orderIdStr);

            log.info("[PAYLETTER] callback received orderId={}", orderId);

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


            if(orderId == null){
                throw new IllegalStateException("orderNumber 없음");
            }

            log.info("[PAYLETTER] callback parsed  orderNumber={}, userId={}, amount={}, tid={}, cid={}, payhash={}",
                    orderId,userId, amount, tid, cid, payhash);


            // payhash 검증
            // payhash = SHA256(user_id + amount + tid + 결제요청 API Key)
            String apiKey = properties.getPaymentApiKey();
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalStateException("paymentApiKey 설정 없음");
            }

            String expected = PayletterHashUtil.makePayhash(userId, amount, tid, apiKey);

            log.info("[PAYLETTER] callback payhash check expected={}, actual={}", expected, payhash);

            if (!expected.equalsIgnoreCase(payhash.trim())) {
                throw new IllegalStateException("payhash 검증 실패");
            }

            //payload JSON 변환
            String payloadJson = null;
            try {
                payloadJson = objectMapper.writeValueAsString(payload);
            } catch (Exception e) {
                log.warn("[PAYLETTER] payload JSON 변환 실패", e);
            }

            // 중복 콜백 방지 DB업데이트
            int updated = orderShopMapper.updatePayletterCallbackSuccessIfNotPaid(
                    orderId,
                    payloadJson,
                    tid,
                    cid
            );

            // 이미 처리된 콜백이면 종료
            if(updated != 1){
                log.info("[PAYLETTER] already processed orderId={}", orderId);
                return;

            }
            log.info("[PAYLETTER] callback processed orderId={}", orderId);


        } catch (Exception e) {
            log.error("[PAYLETTER] callback error payload={}", payload, e);

        }
    }

    // 내부용 주문 취소
//    @Transactional
//    public PayletterCancelResDto cancel(String userId, String tid, String pgCode, String ipAddr) {
//
//        PayletterCancelReqDto req = PayletterCancelReqDto.builder()
//                .clientId(properties.getClientId())
//                .userId(userId)
//                .tid(tid)
//                .pgCode(pgCode)
//                .ipAddr(ipAddr)
//                .build();
//
//        return payletterClient.cancelPayment(req);
//    }

    // order 취소 호출
//    @Transactional
//    public PayletterCancelResDto cancel(UUID orderId) {
//        if (orderId == null) throw new IllegalArgumentException("orderId is null");
//
//        String ipAddr = clientIpProvider.getClientIp();
//        if (ipAddr == null || ipAddr.isBlank()) {
//            throw new IllegalArgumentException("ipAddr is null");
//        }
//
//        return cancel(orderId, ipAddr);
//    }
    // 결제 취소(payletter)
    @Transactional
    public PayletterCancelResult cancel(UUID orderId){

        if (orderId == null) throw new IllegalArgumentException("orderId is null");

        String ipAddr = clientIpProvider.getClientIp();
        if (ipAddr == null || ipAddr.isBlank()) throw new IllegalArgumentException("ipAddr is null");

        Map<String, Object> orderInfo = orderAdminMapper.selectOrderPaymentInfo(orderId);
        if(orderInfo == null) throw new IllegalStateException("주문 없음 orderId="+orderId);

        String pgProvider = (String) orderInfo.get("pg_provider");
        if(!"PAYLETTER".equalsIgnoreCase(pgProvider)){
            throw new IllegalStateException("PAYLETTER 주문 아님");
        }

        String orderNumber = (String) orderInfo.get("order_number");

        // userId 생성
        String phone = (String) orderInfo.get("customer_phone");
        String email = (String) orderInfo.get("customer_email");

        String userId = (phone != null && !phone.isBlank())
                ? phone.replaceAll("[^0-9]", "")
                : (email != null && !email.isBlank() ? email : orderNumber);

        // ===== 1. 금액 계산 =====
        int finalPrice = ((Number) orderInfo.get("final_price")).intValue();

        int alreadyCanceled = orderInfo.get("cancel_amount") != null
                ? ((Number) orderInfo.get("cancel_amount")).intValue()
                : 0;

        int remainAmount = finalPrice - alreadyCanceled;

        if (remainAmount <= 0) {
            throw new IllegalStateException("이미 전액 취소된 주문");
        }

        // ===== 2. 예약상품 여부 =====
        List<OrderProductListGetResDto> items = orderAdminMapper.selectOrderProductList(orderId);

        boolean isReservation = items.stream()
                .anyMatch(item -> Boolean.TRUE.equals(item.getIsReservation()));

        int cancelAmount;
        int cancelFee = 0;

        if (isReservation) {
            cancelAmount = remainAmount;
            log.info("[예약상품] 전액환불 orderId={}, amount={}", orderId, cancelAmount);

        } else {
            Timestamp ts = (Timestamp) orderInfo.get("ordered_at");
            LocalDateTime orderedAt = ts.toLocalDateTime();

            long days = ChronoUnit.DAYS.between(
                    orderedAt.toLocalDate(),
                    LocalDate.now()
            );

            cancelFee = (days <= 7)
                    ? 1000
                    : (int) Math.floor(remainAmount * 0.1);

            cancelAmount = Math.max(remainAmount - cancelFee, 0);

            log.info("[일반상품] orderId={}, days={}, fee={}, refund={}",
                    orderId, days, cancelFee, cancelAmount);
        }

        // ===== 3. 거래조회 (당일 포함) =====
        PayletterTransactionListResDto txRes = payletterClient.getTransactionList(
                properties.getClientId(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                "transaction",
                null,
                orderNumber
        );

        if (txRes == null || !Boolean.TRUE.equals(txRes.isSuccess())) {
            throw new IllegalStateException("transaction 조회 실패");
        }

        PayletterTransactionItemDto txItem = txRes.getList().stream()
                .filter(tx -> tx.getTid() != null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("유효한 거래 없음"));

        String tid = txItem.getTid();
        String pgCode = txItem.getPgCode();

        // ===== 4. 부분취소 요청 =====
        PayletterPartialCancelReqDto req = PayletterPartialCancelReqDto.builder()
                .pgCode(pgCode)
                .clientId(properties.getClientId())
                .userId(userId)
                .tid(tid)
                .amount(cancelAmount)
                .taxfreeAmount(0)
                .taxAmount(0)
                .ipAddr(ipAddr)
                .build();

        PayletterCancelResDto res = payletterClient.partialCancel(req);

        if (res == null || !res.isCanceled()) {
            throw new IllegalStateException("PG 취소 실패");
        }

        // ===== 5. DB 업데이트 =====
        try {
            Map<String, Object> payload = Map.of(
                    "cancelAmount", cancelAmount,
                    "cancelFee", cancelFee,
                    "tid", tid
            );

            String payloadJson = objectMapper.writeValueAsString(payload);

            orderAdminMapper.updateOrderCancelSuccess(orderId,
                    cancelAmount,
                    cancelFee,
                    payloadJson);

        } catch (Exception e) {
            log.error("cancel payload 저장 실패", e);
        }

        log.info("[PAYLETTER CANCEL SUCCESS] orderId={}, refund={}", orderId, cancelAmount);

        return new PayletterCancelResult(cancelAmount, cancelFee, res);
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


        PayletterTransactionListResDto res = payletterClient.getTransactionList(properties.getClientId(),
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
