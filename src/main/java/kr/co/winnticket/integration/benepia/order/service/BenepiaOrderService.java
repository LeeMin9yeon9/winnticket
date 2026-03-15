package kr.co.winnticket.integration.benepia.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.integration.benepia.order.client.BenepiaClient;
import kr.co.winnticket.integration.benepia.order.dto.*;
import kr.co.winnticket.integration.benepia.props.BenepiaProperties;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import kr.co.winnticket.order.admin.dto.OrderAdminDetailGetResDto;
import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.product.admin.dto.ProductDetailGetResDto;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    private final OrderMapper orderMapper;

    private void createJsonFile(BenepiaOrderRequest req) {

        try {

            ObjectMapper mapper = new ObjectMapper();

            String date = LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            String fileName =
                    props.getKcpCoCd() + "_03_orders_" + date + "_001.json";

            // 로컬 프로젝트 폴더
            String path = System.getProperty("user.dir") + "/benepia/" + fileName;


            File file = new File(path);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, req);

            log.info("[BENEPIA] JSON FILE CREATED = {}", path);

        } catch (Exception e) {

            log.error("[BENEPIA] JSON FILE CREATE FAIL", e);
            throw new RuntimeException("베네피아 JSON 생성 실패", e);

        }
    }

    public void sendOrder(
            OrderAdminDetailGetResDto order,
            List<OrderProductListGetResDto> items,
            BenepiaDecryptedParamDto bene){

        if(items.isEmpty()){
            return;
        }

        OrderProductListGetResDto item = items.get(0);

        BenepiaOrderRequest req = new BenepiaOrderRequest();

        req.setKcpCoCd(props.getKcpCoCd());
        req.setCoopCoCd(props.getCustCoCd());

        req.setBenefitId(bene.getBenefit_id());
        req.setCoCd(bene.getSitecode());

        BenepiaOrderRequest.Order orderInfo =
                new BenepiaOrderRequest.Order();

        orderInfo.setOrdId(order.getOrderNumber());

        String orderName = items.get(0).getProductName();

        if(items.size() > 1){
            orderName += " 외 " + (items.size()-1) + "건";
        }

        orderInfo.setOrdNm(orderName);

        orderInfo.setOrdPrc(order.getFinalPrice());
        orderInfo.setOrgnPrc(order.getTotalPrice());

        orderInfo.setOrdDt(
                order.getOrderedAt()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );

        orderInfo.setOrdDtlUrl("https://www.winnticket.store/orders/shop/" + order.getOrderNumber());
        orderInfo.setOrdDtlUrlTyp("Y");

        orderInfo.setOrdDtlUrlMobl("https://www.winnticket.store/orders/shop/" + order.getOrderNumber());
        orderInfo.setOrdDtlUrlTypMobl("Y");

        req.setOrder(orderInfo);

        BenepiaOrderRequest.Payment payment =
                new BenepiaOrderRequest.Payment();

        switch (order.getPaymentMethod()){
            case CARD, VIRTUAL_ACCOUNT, KAKAOPAY -> payment.setSttlMeanId("9");
            case POINT -> payment.setSttlMeanId("10");
            case GIFT -> payment.setSttlMeanId("63");
        }

        payment.setSttlPrc(order.getFinalPrice());

        req.setPayments(List.of(payment));

        List<BenepiaOrderRequest.Product> products =
                new ArrayList<>();

        for(OrderProductListGetResDto p : items){

            BenepiaOrderRequest.Product product =
                    new BenepiaOrderRequest.Product();

            ProductDetailGetResDto productDetail = productMapper.selectProductDetail(p.getProductId());

            product.setPrdId(productDetail.getId().toString());

            product.setPrdNm(productDetail.getName());

            product.setQty(p.getQuantity());

            product.setPrdPrc(p.getUnitPrice());
            product.setPrdOrgnPrc(p.getUnitPrice());

            product.setPrdDtlUrl("https://www.winnticket.store/product/" + productDetail.getCode());
            product.setPrdDtlUrlTyp("Y");

            product.setPrdDtlUrlMobl("https://www.winnticket.store/product/" + productDetail.getCode());
            product.setPrdDtlUrlTypMobl("Y");

            product.setPrdImgUrl(productDetail.getImageUrl().get(0));
            product.setPrdImgUrlMobl(productDetail.getImageUrl().get(0));

            product.setPrdType("00");
            product.setPrdGb("03");

            products.add(product);
        }

        req.setProducts(products);

        validatePrice(req);

        createJsonFile(req);

        client.sendOrder(req);
    }

    public void cancelOrder(
            OrderAdminDetailGetResDto order,
            List<OrderProductListGetResDto> items,
            BenepiaDecryptedParamDto bene){

        if(bene == null){
            return;
        }

        BenepiaCancelRequest req = new BenepiaCancelRequest();

        req.setKcpCoCd(props.getKcpCoCd());
        req.setCoopCoCd(props.getCustCoCd());

        req.setBenefitId(bene.getBenefit_id());
        req.setCoCd(bene.getSitecode());

        BenepiaCancelRequest.OrderCancel cancel =
                new BenepiaCancelRequest.OrderCancel();

        cancel.setOrdId(order.getOrderNumber());
        cancel.setOrdNm(items.get(0).getProductName());

        cancel.setCnclPrc(order.getFinalPrice());
        cancel.setOrgnCnclPrc(order.getTotalPrice());

        cancel.setCnclDt(
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        );

        req.setOrderCancel(cancel);

        BenepiaCancelRequest.Payment payment =
                new BenepiaCancelRequest.Payment();

        payment.setSttlMeanId("10");
        payment.setSttlPrc(order.getFinalPrice());

        req.setPayments(List.of(payment));

        List<BenepiaCancelRequest.Product> products =
                new ArrayList<>();

        for(OrderProductListGetResDto p : items){

            BenepiaCancelRequest.Product product =
                    new BenepiaCancelRequest.Product();

            ProductDetailGetResDto productDetail = productMapper.selectProductDetail(p.getId());

            product.setPrdId(productDetail.getId().toString());

            product.setPrdNm(productDetail.getName());

            product.setQty(p.getQuantity());

            product.setPrdPrc(p.getUnitPrice());
            product.setPrdOrgnPrc(p.getUnitPrice());

            product.setPrdType("00");

            product.setPartCnclYn("N");

            products.add(product);
        }

        req.setProducts(products);

        validateCancelPrice(req);

        client.cancelOrder(req);
    }

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