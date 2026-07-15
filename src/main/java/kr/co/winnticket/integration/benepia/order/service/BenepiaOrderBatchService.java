package kr.co.winnticket.integration.benepia.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.integration.benepia.order.client.BenepiaOrderBatchApiClient;
import kr.co.winnticket.integration.benepia.order.dto.BenepiaCancelRequest;
import kr.co.winnticket.integration.benepia.order.dto.BenepiaOrderBatchRequest;
import kr.co.winnticket.integration.benepia.order.dto.BenepiaOrderRequest;
import kr.co.winnticket.integration.benepia.order.mapper.BenepiaOrderBatchMapper;
import kr.co.winnticket.integration.benepia.props.BenepiaProperties;
import kr.co.winnticket.order.admin.dto.OrderAdminDetailGetResDto;
import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.product.admin.dto.ProductDetailGetResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class BenepiaOrderBatchService {

    private final BenepiaOrderBatchMapper mapper;
    private final BenepiaOrderBatchApiClient client;
    private final BenepiaProperties props;
    private final ObjectMapper objectMapper;
    private final BenepiaProductMappingService mappingService;

    private static final int MAX_SIZE = 10000;

    public void runBatch(LocalDate targetDate) {

        List<Object> body = new ArrayList<>();

        // 하루치 배치 전체에서 재사용할 상품상세 캐시 (동일 상품이 여러 주문에 반복 등장할 때 N+1 방지)
        Map<String, ProductDetailGetResDto> productDetailCache = new HashMap<>();

        // =========================
        // 1. 주문 먼저
        // =========================
        List<OrderAdminDetailGetResDto> orders = mapper.selectBatchOrders(targetDate);

        for (OrderAdminDetailGetResDto order : orders) {
            try {
                List<OrderProductListGetResDto> items = mapper.selectOrderItems(order.getId());

                BenepiaOrderRequest req = buildOrderRequest(order, items, productDetailCache);
                if (req != null) {
                    body.add(req);
                }
            } catch (Exception e) {
                // 주문 1건 실패가 하루치 배치 전체를 막지 않도록 스킵
                log.error("[BENEPIA BATCH] 주문 처리 실패, 스킵 orderId={}", order.getOrderNumber(), e);
            }
        }

        // =========================
        // 2. 취소 나중
        // =========================
        List<OrderAdminDetailGetResDto> cancels = mapper.selectBatchCancels(targetDate);

        for (OrderAdminDetailGetResDto order : cancels) {
            try {
                List<OrderProductListGetResDto> items = mapper.selectOrderItems(order.getId());

                BenepiaCancelRequest req = buildCancelRequest(order, items, productDetailCache);
                if (req != null) {
                    body.add(req);
                }
            } catch (Exception e) {
                // 취소 1건 실패가 하루치 배치 전체를 막지 않도록 스킵
                log.error("[BENEPIA BATCH] 취소 처리 실패, 스킵 orderId={}", order.getOrderNumber(), e);
            }
        }

        if (body.isEmpty()) {
            log.info("[BENEPIA BATCH] 대상 없음. targetDate={}", targetDate);
            return;
        }

        // =========================
        // 3. 10000건 단위 분할 업로드
        // =========================
        int fileSeq = 0;
        for (int start = 0; start < body.size(); start += MAX_SIZE) {
            int end = Math.min(start + MAX_SIZE, body.size());
            List<Object> chunk = new ArrayList<>(body.subList(start, end));

            BenepiaOrderBatchRequest batch = new BenepiaOrderBatchRequest();
            batch.setBody(chunk);

            File file = createFile(batch, fileSeq);

            client.uploadBatch(file);

            log.info("[BENEPIA BATCH SUCCESS] file={}, count={}", file.getName(), chunk.size());

            fileSeq++;
        }
    }

    // =========================
    // 파일 생성
    // =========================
    private File createFile(BenepiaOrderBatchRequest batch, int fileSeq) {

        try {
            String date = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            String fileName =
                    props.getKcpCoCd() + "_ticket_orders_" + date + "_" + fileSeq + ".json";

            String path = System.getProperty("user.dir") + "/benepia/" + fileName;

            File file = new File(path);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, batch);

            log.info("[BENEPIA BATCH FILE CREATED] {}", path);

            return file;

        } catch (Exception e) {
            log.error("[BENEPIA BATCH FILE CREATE FAIL]", e);
            throw new RuntimeException(e);
        }
    }

    // =========================
    // null 방어 유틸
    // =========================
    private String nvl(String val) {
        return val == null ? "" : val;
    }

    private Integer nvl(Integer val) {
        return val == null ? 0 : val;
    }

    // =========================
    // 주문 Request 생성
    // =========================
    public BenepiaOrderRequest buildOrderRequest(
            OrderAdminDetailGetResDto order,
            List<OrderProductListGetResDto> items,
            Map<String, ProductDetailGetResDto> productDetailCache) {

        BenepiaOrderRequest req = new BenepiaOrderRequest();

        req.setKcpCoCd(nvl(props.getKcpCoCd()));
        req.setCoopCoCd(nvl(props.getCustCoCd()));
        order.setBenepiaId("testtravel");
        req.setBenefitId(nvl(order.getBenepiaId()));
        req.setCoCd("5555");

        BenepiaOrderRequest.Order orderInfo = new BenepiaOrderRequest.Order();

        orderInfo.setOrdId(nvl(order.getOrderNumber()));

        String orderName = nvl(items.get(0).getProductName());
        if (items.size() > 1) {
            orderName += " 외 " + (items.size() - 1) + "건";
        }

        orderInfo.setOrdNm(orderName);
        orderInfo.setOrdPrc(nvl(order.getFinalPrice()));
        orderInfo.setOrgnPrc(nvl(order.getFinalPrice()));

        orderInfo.setOrdDt(
                order.getOrderedAt().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );

        orderInfo.setPtnAccntId("");

        String orderUrl = "/order-lookup/" + order.getChannelId() + "/" + order.getOrderNumber();

        orderInfo.setOrdDtlUrl(nvl(orderUrl));
        orderInfo.setOrdDtlUrlTyp("Y");
        orderInfo.setOrdDtlUrlMobl(nvl(orderUrl));
        orderInfo.setOrdDtlUrlTypMobl("Y");

        req.setOrder(orderInfo);

        List<BenepiaOrderRequest.Payment> payments = new ArrayList<>();

        int totalPrice = nvl(order.getFinalPrice());
        int pointAmount = nvl(order.getPointAmount());
        int remainAmount = totalPrice - pointAmount;

        if (pointAmount > 0) {
            BenepiaOrderRequest.Payment pointPayment = new BenepiaOrderRequest.Payment();
            pointPayment.setSttlMeanId("10");
            pointPayment.setSttlPrc(pointAmount);
            payments.add(pointPayment);
        }

        if (remainAmount > 0) {
            BenepiaOrderRequest.Payment mainPayment = new BenepiaOrderRequest.Payment();

            switch (order.getPaymentMethod()) {
                case CARD, VIRTUAL_ACCOUNT, KAKAOPAY -> mainPayment.setSttlMeanId("9");
                case GIFT -> mainPayment.setSttlMeanId("63");
                case POINT -> mainPayment.setSttlMeanId("10");
                default -> mainPayment.setSttlMeanId("9");
            }

            mainPayment.setSttlPrc(remainAmount);
            payments.add(mainPayment);
        }

        req.setPayments(payments);

        List<BenepiaOrderRequest.Product> products = new ArrayList<>();

        for (BenepiaProductMappingService.MergedProductLine m
                : mappingService.mergeDuplicateOptions(items, productDetailCache)) {

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

            String img = "";
            if (detail.getImageUrl() != null && !detail.getImageUrl().isEmpty()) {
                img = detail.getImageUrl().get(0);
            }

            product.setPrdImgUrl(nvl(img));
            product.setPrdImgUrlMobl(nvl(img));

            product.setPrdType("10");
            product.setPrdGb("03");

            product.setUseFrDy("");
            product.setUseToDy("");
            product.setRoomTypNm("");
            product.setAdultCnt(m.adultCnt);
            product.setYouthCnt(m.youthCnt);
            product.setChildCnt(m.childCnt);
            product.setNightCnt(0);
            product.setWeekendYn("");
            product.setSeasonYn("");
            product.setRepResvNm("");

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

        return req;
    }

    // =========================
    // 취소 Request 생성
    // =========================
    public BenepiaCancelRequest buildCancelRequest(
            OrderAdminDetailGetResDto order,
            List<OrderProductListGetResDto> items,
            Map<String, ProductDetailGetResDto> productDetailCache) {

        BenepiaCancelRequest req = new BenepiaCancelRequest();

        req.setKcpCoCd(nvl(props.getKcpCoCd()));
        req.setCoopCoCd(nvl(props.getCustCoCd()));
        order.setBenepiaId("testtravel");
        req.setBenefitId(nvl(order.getBenepiaId()));
        req.setCoCd("5555");

        BenepiaCancelRequest.OrderCancel cancel = new BenepiaCancelRequest.OrderCancel();

        cancel.setOrdId(nvl(order.getOrderNumber()));

        String orderName = nvl(items.get(0).getProductName());
        if (items.size() > 1) {
            orderName += " 외 " + (items.size() - 1) + "건";
        }

        cancel.setOrdNm(orderName);
        cancel.setCnclPrc(nvl(order.getFinalPrice()));
        cancel.setOrgnCnclPrc(nvl(order.getFinalPrice()));

        if (order.getCanceledAt() == null) {
            throw new RuntimeException("베네피아 취소일시 없음");
        }

        cancel.setCnclDt(
                order.getCanceledAt().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );

        req.setOrderCancel(cancel);

        List<BenepiaCancelRequest.Payment> payments = new ArrayList<>();

        int totalPrice = nvl(order.getFinalPrice());
        int pointAmount = nvl(order.getPointAmount());
        int remainAmount = totalPrice - pointAmount;

        if (pointAmount > 0) {
            BenepiaCancelRequest.Payment pointPayment = new BenepiaCancelRequest.Payment();
            pointPayment.setSttlMeanId("10");
            pointPayment.setSttlPrc(pointAmount);
            payments.add(pointPayment);
        }

        if (remainAmount > 0) {
            BenepiaCancelRequest.Payment mainPayment = new BenepiaCancelRequest.Payment();

            switch (order.getPaymentMethod()) {
                case CARD, VIRTUAL_ACCOUNT, KAKAOPAY -> mainPayment.setSttlMeanId("9");
                case GIFT -> mainPayment.setSttlMeanId("63");
                case POINT -> mainPayment.setSttlMeanId("10");
                default -> mainPayment.setSttlMeanId("9");
            }

            mainPayment.setSttlPrc(remainAmount);
            payments.add(mainPayment);
        }

        req.setPayments(payments);

        List<BenepiaCancelRequest.Product> products = new ArrayList<>();

        for (BenepiaProductMappingService.MergedProductLine m
                : mappingService.mergeDuplicateOptions(items, productDetailCache)) {

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

            String img = "";
            if (detail.getImageUrl() != null && !detail.getImageUrl().isEmpty()) {
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

        return req;
    }

    // =========================
    // 검증
    // =========================
    private void validatePrice(BenepiaOrderRequest req) {
        int paymentSum = req.getPayments()
                .stream()
                .mapToInt(BenepiaOrderRequest.Payment::getSttlPrc)
                .sum();

        if (paymentSum != req.getOrder().getOrdPrc()) {
            throw new RuntimeException("베네피아 금액 불일치");
        }
    }

    private void validateCancelPrice(BenepiaCancelRequest req) {
        int paymentSum = req.getPayments()
                .stream()
                .mapToInt(BenepiaCancelRequest.Payment::getSttlPrc)
                .sum();

        if (paymentSum != req.getOrderCancel().getCnclPrc()) {
            throw new RuntimeException("베네피아 취소 금액 불일치");
        }
    }
}
