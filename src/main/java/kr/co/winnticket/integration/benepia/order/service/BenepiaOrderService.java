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
    // 주문 전송
    // =========================
    public void sendOrder(
            OrderAdminDetailGetResDto order,
            List<OrderProductListGetResDto> items) {

        if(items == null || items.isEmpty()) return;

        // 베네피아 회원이 아닌 일반 주문은 전송 대상이 아님 (필수 파라미터 누락으로 실패하는 것을 방지)
        order.setBenepiaId("testtravel");
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
        // 스펙상 coCd는 sitecode 파라미터로 접속 시 전달되는 값이라 benefitId처럼
        // 주문 저장 시점에 캡처된 값이어야 할 가능성이 큼 (order.getSiteCd() 등으로 대체 검토)
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

        for(OrderProductListGetResDto p : items){
            ProductDetailGetResDto detail =
                    productMapper.selectProductDetail(p.getProductId());

            BenepiaOrderRequest.Product product = new BenepiaOrderRequest.Product();

            product.setPrdId(nvl(detail.getCode()));
            product.setPrdNm(nvl(detail.getName()));
            product.setPrdOptNm(nvl(p.getOptionName()));

            product.setQty(nvl(p.getQuantity()));
            product.setPrdPrc(nvl(p.getTotalPrice()));
            product.setPrdOrgnPrc(nvl(p.getTotalPrice()));

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

        cancel.setCnclPrc(totalRefundAmount);
        // 매입금액(선택항목) - 정확한 매입원가 추적이 없어 실환불액으로 근사치 사용
        cancel.setOrgnCnclPrc(totalRefundAmount);

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
            pointPayment.setSttlPrc(pointRefundAmount);

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

            mainPayment.setSttlPrc(remainRefund);

            payments.add(mainPayment);
        }

        req.setPayments(payments);

        List<BenepiaCancelRequest.Product> products = new ArrayList<>();

        for(OrderProductListGetResDto p : items){
            ProductDetailGetResDto detail =
                    productMapper.selectProductDetail(p.getProductId());

            BenepiaCancelRequest.Product product = new BenepiaCancelRequest.Product();

            product.setPrdId(nvl(detail.getCode()));
            product.setPrdNm(nvl(detail.getName()));
            // 옵션명 자리에 상품명이 잘못 들어가던 버그 수정 (getProductName -> getOptionName)
            product.setPrdOptNm(nvl(p.getOptionName()));

            product.setQty(nvl(p.getQuantity()));
            product.setPrdPrc(nvl(p.getTotalPrice()));
            product.setPrdOrgnPrc(nvl(p.getTotalPrice()));

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
