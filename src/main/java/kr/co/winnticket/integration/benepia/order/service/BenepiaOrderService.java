package kr.co.winnticket.integration.benepia.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.benepia.crypto.BenepiaSeedEcbCrypto;
import kr.co.winnticket.integration.benepia.order.client.BenepiaClient;
import kr.co.winnticket.integration.benepia.order.dto.*;
import kr.co.winnticket.integration.benepia.props.BenepiaProperties;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import kr.co.winnticket.order.admin.dto.OrderAdminDetailGetResDto;
import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.product.admin.dto.ProductDetailGetResDto;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class BenepiaOrderService {

    private final BenepiaClient client;
    private final BenepiaProperties props;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;

    // =========================
    // null 방어 유틸
    // =========================
    private String nvl(String val){
        return val == null ? "" : val;
    }

    private Integer nvl(Integer val){
        return val == null ? 0 : val;
    }

    // =========================
    // CASE6 대응:
    // 1) prdId + optionIds(실제 옵션 고유 식별값)가 완전히 같은 라인만 qty/금액을 합산
    // 2) 같은 prdId 내에서 옵션명은 같아 보이지만 optionIds가 서로 다른 경우
    //    (이름만 같고 실제로는 다른 옵션인 케이스) → "옵션명&prdOptId=옵션ID"로 구분해서 전송
    // =========================
    private static class MergedProductLine {
        ProductDetailGetResDto detail;
        String displayOptionName; // 원본 옵션명 (DB 표시값)
        String optionIds;         // 실제 옵션 고유 식별값 (병합 판단 기준)
        String prdOptNm;          // 베네피아로 실제 전송할 옵션명 (충돌 시 &prdOptId= 접미)
        int qty;
        int prdPrc;
    }

    private List<MergedProductLine> mergeDuplicateOptions(List<OrderProductListGetResDto> items) {
        Map<String, MergedProductLine> merged = new LinkedHashMap<>();
        // prdId -> (옵션명 -> 그 옵션명으로 나타난 서로 다른 optionIds 집합) : 이름 충돌 감지용
        Map<String, Map<String, Set<String>>> nameCollisionCheck = new HashMap<>();

        for (OrderProductListGetResDto p : items) {
            ProductDetailGetResDto detail = productMapper.selectProductDetail(p.getProductId());

            String prdId = nvl(detail.getCode());
            String optionName = nvl(p.getOptionName());
            String optionIds = nvl(p.getOptionIds());

            // 진짜 동일한 상품+옵션(prdId+optionIds)인 경우에만 병합 (스펙 2번 케이스)
            String mergeKey = prdId + "&prdOptId=" + optionIds;

            MergedProductLine line = merged.get(mergeKey);
            if (line == null) {
                line = new MergedProductLine();
                line.detail = detail;
                line.displayOptionName = optionName;
                line.optionIds = optionIds;
                merged.put(mergeKey, line);
            }

            line.qty += nvl(p.getQuantity());
            line.prdPrc += nvl(p.getTotalPrice());

            nameCollisionCheck
                    .computeIfAbsent(prdId, k -> new HashMap<>())
                    .computeIfAbsent(optionName, k -> new HashSet<>())
                    .add(optionIds);
        }

        // 같은 prdId 내에서 옵션명이 같은데 optionIds가 서로 다르면 (스펙 1번 케이스) 구분값 부여
        for (MergedProductLine line : merged.values()) {
            String prdId = nvl(line.detail.getCode());
            Set<String> idsForThisName = nameCollisionCheck.get(prdId).get(line.displayOptionName);

            if (idsForThisName.size() > 1 && !line.optionIds.isBlank()) {
                line.prdOptNm = line.displayOptionName + "&prdOptId=" + line.optionIds;
            } else {
                line.prdOptNm = line.displayOptionName;
            }
        }

        return new ArrayList<>(merged.values());
    }

    // =========================
    // 주문 전송
    // =========================
    public void sendOrder(
            OrderAdminDetailGetResDto order,
            List<OrderProductListGetResDto> items) {

        if(items == null || items.isEmpty()) return;
        order.setBenepiaId("testtravel");
        // 베네피아 회원이 아닌 일반 주문은 전송 대상이 아님 (필수 파라미터 누락으로 실패하는 것을 방지)
        if(order.getBenepiaId() == null || order.getBenepiaId().isBlank()) return;

        try {
            sendOrderInternal(order, items);
        } catch (Exception e) {
            // 베네피아 연동 실패가 결제/발권 트랜잭션에 영향을 주지 않도록 여기서 흡수
            log.error("[BENEPIA] 주문 전송 실패 orderId={}", order.getOrderNumber(), e);
        }
    }

    private void sendOrderInternal(
            OrderAdminDetailGetResDto order,
            List<OrderProductListGetResDto> items) {

        BenepiaOrderRequest req = new BenepiaOrderRequest();

        req.setKcpCoCd(nvl(props.getKcpCoCd()));
        req.setCoopCoCd(nvl(props.getCustCoCd()));
        req.setBenefitId(nvl(order.getBenepiaId()));
        req.setCoCd("5555");

        // =========================
        // 주문 정보
        // =========================
        BenepiaOrderRequest.Order orderInfo = new BenepiaOrderRequest.Order();

        orderInfo.setOrdId(nvl(order.getOrderNumber()));

        String orderName = nvl(items.get(0).getProductName());
        if(items.size() > 1){
            orderName += " 외 " + (items.size()-1) + "건";
        }

        orderInfo.setOrdNm(orderName);
        orderInfo.setOrdPrc(nvl(order.getFinalPrice()));
        orderInfo.setOrgnPrc(nvl(order.getFinalPrice()));

        orderInfo.setOrdDt(
                order.getOrderedAt()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );

        orderInfo.setPtnAccntId("");

        String orderUrl = "/order-lookup/" + order.getChannelId() + "/" + order.getOrderNumber();

        orderInfo.setOrdDtlUrl(nvl(orderUrl));
        orderInfo.setOrdDtlUrlTyp("Y");

        orderInfo.setOrdDtlUrlMobl(nvl(orderUrl));
        orderInfo.setOrdDtlUrlTypMobl("Y");

        req.setOrder(orderInfo);

        // =========================
        // 결제 정보
        // =========================
        List<BenepiaOrderRequest.Payment> payments = new ArrayList<>();

        int totalPrice = nvl(order.getFinalPrice());
        int pointAmount = nvl(order.getPointAmount());
        int remainAmount = totalPrice - pointAmount;

        // =========================
        // 1. 포인트 결제
        // =========================
        if(pointAmount > 0){
            BenepiaOrderRequest.Payment pointPayment = new BenepiaOrderRequest.Payment();
            pointPayment.setSttlMeanId("10"); // 포인트
            pointPayment.setSttlPrc(pointAmount);

            payments.add(pointPayment);
        }

        // =========================
        // 2. 실제 결제수단
        // =========================
        if(remainAmount > 0){
            BenepiaOrderRequest.Payment mainPayment = new BenepiaOrderRequest.Payment();

            switch (order.getPaymentMethod()){
                case CARD, VIRTUAL_ACCOUNT, KAKAOPAY -> mainPayment.setSttlMeanId("9");
                case GIFT -> mainPayment.setSttlMeanId("63");
                case POINT -> mainPayment.setSttlMeanId("10"); // 전체 포인트 결제 케이스
                default -> mainPayment.setSttlMeanId("9");
            }

            mainPayment.setSttlPrc(remainAmount);

            payments.add(mainPayment);
        }

        req.setPayments(payments);

        // =========================
        // 상품 정보
        // =========================
        List<BenepiaOrderRequest.Product> products = new ArrayList<>();

        for(MergedProductLine m : mergeDuplicateOptions(items)){
            ProductDetailGetResDto detail = m.detail;

            BenepiaOrderRequest.Product product = new BenepiaOrderRequest.Product();

            product.setPrdId(nvl(detail.getCode()));
            product.setPrdNm(nvl(detail.getName()));
            product.setPrdOptNm(m.prdOptNm);

            product.setQty(m.qty);
            product.setPrdPrc(m.prdPrc);
            product.setPrdOrgnPrc(m.prdPrc);

            String productUrl = "/product/" + detail.getCode() + "?channel=BENE";

            product.setPrdDtlUrl(nvl(productUrl));
            product.setPrdDtlUrlTyp("Y");

            product.setPrdDtlUrlMobl(nvl(productUrl));
            product.setPrdDtlUrlTypMobl("Y");

            // 이미지 null 방어
            String img = "";
            if(detail.getImageUrl() != null && !detail.getImageUrl().isEmpty()){
                img = detail.getImageUrl().get(0);
            }

            product.setPrdImgUrl(nvl(img));
            product.setPrdImgUrlMobl(nvl(img));

            product.setPrdType("10");
            product.setPrdGb("03");

            product.setUseFrDy("");
            product.setUseToDy("");
            product.setRoomTypNm("");
            product.setAdultCnt(0);
            product.setYouthCnt(0);
            product.setChildCnt(0);
            product.setNightCnt(0);
            product.setWeekendYn("");
            product.setSeasonYn("");
            product.setRepResvNm("");

            // =========================
            // 항공 예약정보 (무조건 "" 세팅)
            // =========================
            List<BenepiaOrderRequest.AirResvInfo> airList = new ArrayList<>();
            BenepiaOrderRequest.AirResvInfo air = new BenepiaOrderRequest.AirResvInfo();

            air.setResvType("");
            air.setResvNo("");
            air.setTicketNo("");
            air.setDepPointCity("");
            air.setDepPointNation("");
            air.setDestPointCity("");
            air.setDestPointNation("");
            air.setAirDt("");
            air.setAirline("");
            air.setSeatClass("");
            air.setPassengerTyp("");
            airList.add(air);
            product.setAirResvInfoList(airList);

            products.add(product);
        }

        req.setProducts(products);

        if(!validatePrice(req)){
            log.error("[BENEPIA] 금액 불일치로 주문 전송 스킵 orderId={}", order.getOrderNumber());
            return;
        }

        createJsonFile(req);

        client.sendOrder(req);
    }

    // =========================
    // 취소 전송
    // =========================
    public void cancelOrder(
            OrderAdminDetailGetResDto order,
            List<OrderProductListGetResDto> items,
            int totalRefundAmount,
            int pointRefundAmount){

        order.setBenepiaId("testtravel");
        if(order.getBenepiaId() == null || order.getBenepiaId().isBlank()
                || items == null || items.isEmpty()) return;

        try {
            cancelOrderInternal(order, items, totalRefundAmount, pointRefundAmount);
        } catch (Exception e) {
            // 베네피아 연동 실패가 취소 트랜잭션(재고/쿠폰 복구 등)에 영향을 주지 않도록 여기서 흡수
            log.error("[BENEPIA] 주문 취소 전송 실패 orderId={}", order.getOrderNumber(), e);
        }
    }

    private void cancelOrderInternal(
            OrderAdminDetailGetResDto order,
            List<OrderProductListGetResDto> items,
            int totalRefundAmount,
            int pointRefundAmount){

        BenepiaCancelRequest req = new BenepiaCancelRequest();

        req.setKcpCoCd(nvl(props.getKcpCoCd()));
        req.setCoopCoCd(nvl(props.getCustCoCd()));
        req.setBenefitId(nvl(order.getBenepiaId()));
        req.setCoCd("5555");

        BenepiaCancelRequest.OrderCancel cancel = new BenepiaCancelRequest.OrderCancel();

        cancel.setOrdId(nvl(order.getOrderNumber()));

        String orderName = nvl(items.get(0).getProductName());
        if(items.size() > 1){
            orderName += " 외 " + (items.size()-1) + "건";
        }

        cancel.setOrdNm(orderName);

        cancel.setCnclPrc(62900);
        // 매입금액(선택항목) - 정확한 매입원가 추적이 없어 실환불액으로 근사치 사용
        cancel.setOrgnCnclPrc(62900);

        cancel.setCnclDt(
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );

        req.setOrderCancel(cancel);


        // =========================
        // 결제 정보 (수수료 차감 후 실제 환불액 기준)
        // =========================
        List<BenepiaCancelRequest.Payment> payments = new ArrayList<>();

        int remainRefund = totalRefundAmount - pointRefundAmount;

        // =========================
        // 1. 포인트 환불
        // =========================
        if(pointRefundAmount > 0){
            BenepiaCancelRequest.Payment pointPayment = new BenepiaCancelRequest.Payment();
            pointPayment.setSttlMeanId("10"); // 포인트
            pointPayment.setSttlPrc(0);

            payments.add(pointPayment);
        }

        // =========================
        // 2. 그 외 결제수단 환불 (수수료 차감 후 금액)
        // =========================
        if(remainRefund > 0){
            BenepiaCancelRequest.Payment mainPayment = new BenepiaCancelRequest.Payment();

            switch (order.getPaymentMethod()){
                case CARD, VIRTUAL_ACCOUNT, KAKAOPAY -> mainPayment.setSttlMeanId("9");
                case GIFT -> mainPayment.setSttlMeanId("63");
                case POINT -> mainPayment.setSttlMeanId("10"); // 전체 포인트 결제 케이스
                default -> mainPayment.setSttlMeanId("9");
            }

            mainPayment.setSttlPrc(62900);

            payments.add(mainPayment);
        }

        req.setPayments(payments);

        List<BenepiaCancelRequest.Product> products = new ArrayList<>();

        for(MergedProductLine m : mergeDuplicateOptions(items)){
            ProductDetailGetResDto detail = m.detail;

            BenepiaCancelRequest.Product product = new BenepiaCancelRequest.Product();

            product.setPrdId(nvl(detail.getCode()));
            product.setPrdNm(nvl(detail.getName()));
            product.setPrdOptNm(m.prdOptNm);

            product.setQty(m.qty);
            product.setPrdPrc(m.prdPrc);
            product.setPrdOrgnPrc(m.prdPrc);

            String productUrl = "/product/" + detail.getCode() + "?channel=BENE";

            product.setPrdDtlUrl(nvl(productUrl));
            product.setPrdDtlUrlTyp("Y");

            product.setPrdDtlUrlMobl(nvl(productUrl));
            product.setPrdDtlUrlTypMobl("Y");

            // 이미지 null 방어
            String img = "";
            if(detail.getImageUrl() != null && !detail.getImageUrl().isEmpty()){
                img = detail.getImageUrl().get(0);
            }

            product.setPrdImgUrl(nvl(img));
            product.setPrdImgUrlMobl(nvl(img));
            product.setPrdType("10");
            product.setPartCnclYn("");

            products.add(product);
        }

        req.setProducts(products);

        if(!validateCancelPrice(req)){
            log.error("[BENEPIA] 취소 금액 불일치로 취소 전송 스킵 orderId={}", order.getOrderNumber());
            return;
        }

        createJsonFile(req);

        client.cancelOrder(req, order.getOrderNumber());
    }

    // =========================
    // JSON 파일 생성 (실패해도 결제 처리에 영향 없음 - 로그만)
    // =========================
    private void createJsonFile(BenepiaOrderRequest req){

        try{
            String date = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            String fileName =
                    props.getKcpCoCd() + "_03_orders_" + date + "_001.json";

            String path = System.getProperty("user.dir") + "/benepia/" + fileName;

            File file = new File(path);

            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, req);

            log.info("[BENEPIA] JSON FILE CREATED = {}", path);

        }catch (Exception e){
            // 파일 생성 실패는 결제를 막지 않음 (로그만 기록)
            log.error("[BENEPIA] JSON FILE CREATE FAIL (결제는 정상 처리됨)", e);
        }
    }

    // =========================
    // JSON 파일 생성 (실패해도 결제 처리에 영향 없음 - 로그만)
    // =========================
    private void createJsonFile(BenepiaCancelRequest req){

        try{
            String date = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            String fileName =
                    props.getKcpCoCd() + "_03_canc_" + date + "_001.json";

            String path = System.getProperty("user.dir") + "/benepia/" + fileName;

            File file = new File(path);

            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, req);

            log.info("[BENEPIA] JSON FILE CREATED = {}", path);

        }catch (Exception e){
            // 파일 생성 실패는 결제를 막지 않음 (로그만 기록)
            log.error("[BENEPIA] JSON FILE CREATE FAIL (결제는 정상 처리됨)", e);
        }
    }

    // =========================
    // 검증
    // =========================
    private boolean validatePrice(BenepiaOrderRequest req){

        int paymentSum =
                req.getPayments()
                        .stream()
                        .mapToInt(p -> p.getSttlPrc())
                        .sum();

        return paymentSum == req.getOrder().getOrdPrc();
    }

    private boolean validateCancelPrice(BenepiaCancelRequest req){

        int paymentSum =
                req.getPayments()
                        .stream()
                        .mapToInt(p -> p.getSttlPrc())
                        .sum();

        return paymentSum == req.getOrderCancel().getCnclPrc();
    }
}
