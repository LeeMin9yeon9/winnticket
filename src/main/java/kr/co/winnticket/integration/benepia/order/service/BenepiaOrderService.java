package kr.co.winnticket.integration.benepia.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
            List<OrderProductListGetResDto> items,
            BenepiaDecryptedParamDto bene){

        if(items == null || items.isEmpty()) return;

        BenepiaOrderRequest req = new BenepiaOrderRequest();

        req.setKcpCoCd(nvl(props.getKcpCoCd()));
        req.setCoopCoCd(nvl(props.getCustCoCd()));
        req.setBenefitId(nvl(bene.getBenefit_id()));
        req.setCoCd(nvl(bene.getSitecode()));

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
        orderInfo.setOrgnPrc(0);

        orderInfo.setOrdDt(
                order.getOrderedAt()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );

        orderInfo.setPtnAccntId("");

        String orderUrl = "https://www.winnticket.store/orders/shop/" + order.getOrderNumber();

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

            product.setPrdId(nvl(p.getProductCode()));
            product.setPrdNm(nvl(p.getProductName()));
            product.setPrdOptNm(nvl(p.getOptionName()));

            product.setQty(nvl(p.getQuantity()));
            product.setPrdPrc(nvl(p.getTotalPrice()));
            product.setPrdOrgnPrc(0);

            String productUrl = "https://www.winnticket.store/product/" + detail.getCode() + "?channel=BENE";

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
        validatePrice(req);
        createJsonFile(req);

        client.sendOrder(req);
    }

    // =========================
    // 취소 전송
    // =========================
    public void cancelOrder(
            OrderAdminDetailGetResDto order,
            List<OrderProductListGetResDto> items,
            BenepiaDecryptedParamDto bene){

        if(bene == null || items == null || items.isEmpty()) return;

        BenepiaCancelRequest req = new BenepiaCancelRequest();

        req.setKcpCoCd(nvl(props.getKcpCoCd()));
        req.setCoopCoCd(nvl(props.getCustCoCd()));
        req.setBenefitId(nvl(bene.getBenefit_id()));
        req.setCoCd(nvl(bene.getSitecode()));

        BenepiaCancelRequest.OrderCancel cancel = new BenepiaCancelRequest.OrderCancel();

        cancel.setOrdId(nvl(order.getOrderNumber()));

        String orderName = nvl(items.get(0).getProductName());
        if(items.size() > 1){
            orderName += " 외 " + (items.size()-1) + "건";
        }

        cancel.setOrdNm(orderName);

        cancel.setCnclPrc(nvl(order.getFinalPrice()));
        cancel.setOrgnCnclPrc(0);

        cancel.setCnclDt(
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );

        req.setOrderCancel(cancel);


        // =========================
        // 결제 정보
        // =========================
        List<BenepiaCancelRequest.Payment> payments = new ArrayList<>();

        int totalPrice = nvl(order.getFinalPrice());
        int pointAmount = nvl(order.getPointAmount());
        int remainAmount = totalPrice - pointAmount;

        // =========================
        // 1. 포인트 결제
        // =========================
        if(pointAmount > 0){
            BenepiaCancelRequest.Payment pointPayment = new BenepiaCancelRequest.Payment();
            pointPayment.setSttlMeanId("10"); // 포인트
            pointPayment.setSttlPrc(pointAmount);

            payments.add(pointPayment);
        }

        // =========================
        // 2. 실제 결제수단
        // =========================
        if(remainAmount > 0){
            BenepiaCancelRequest.Payment mainPayment = new BenepiaCancelRequest.Payment();

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

        List<BenepiaCancelRequest.Product> products = new ArrayList<>();

        for(OrderProductListGetResDto p : items){
            ProductDetailGetResDto detail =
                    productMapper.selectProductDetail(p.getProductId());

            BenepiaCancelRequest.Product product = new BenepiaCancelRequest.Product();

            product.setPrdId(nvl(p.getProductCode()));
            product.setPrdNm(nvl(p.getProductName()));
            product.setPrdOptNm(nvl(p.getProductName()));

            product.setQty(nvl(p.getQuantity()));
            product.setPrdPrc(nvl(p.getTotalPrice()));
            product.setPrdOrgnPrc(0);

            String productUrl = "https://www.winnticket.store/product/" + detail.getCode() + "?channel=BENE";

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

        validateCancelPrice(req);

        client.cancelOrder(req, order.getOrderNumber());
    }

    // =========================
    // JSON 파일 생성
    // =========================
    private void createJsonFile(BenepiaOrderRequest req){

        try{
            ObjectMapper mapper = new ObjectMapper();

            String date = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            String fileName =
                    props.getKcpCoCd() + "_03_orders_" + date + "_001.json";

            String path = System.getProperty("user.dir") + "/benepia/" + fileName;

            File file = new File(path);

            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, req);

            log.info("[BENEPIA] JSON FILE CREATED = {}", path);

        }catch (Exception e){
            log.error("[BENEPIA] JSON FILE CREATE FAIL", e);
            throw new RuntimeException(e);
        }
    }

    // =========================
    // 검증
    // =========================
    private void validatePrice(BenepiaOrderRequest req){

        int paymentSum =
                req.getPayments()
                        .stream()
                        .mapToInt(p -> p.getSttlPrc())
                        .sum();

        if(paymentSum != req.getOrder().getOrdPrc()){
            throw new RuntimeException("베네피아 금액 불일치");
        }
    }

    private void validateCancelPrice(BenepiaCancelRequest req){

        int paymentSum =
                req.getPayments()
                        .stream()
                        .mapToInt(p -> p.getSttlPrc())
                        .sum();

        if(paymentSum != req.getOrderCancel().getCnclPrc()){
            throw new RuntimeException("베네피아 취소 금액 불일치");
        }
    }
}