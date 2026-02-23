package kr.co.winnticket.order.shop.service;

import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.cart.service.ShopCartService;
import kr.co.winnticket.channels.channel.mapper.ChannelMapper;
import kr.co.winnticket.common.enums.PaymentMethod;
import kr.co.winnticket.common.enums.SmsTemplateCode;
import kr.co.winnticket.integration.payletter.dto.PayletterPaymentResDto;
import kr.co.winnticket.integration.payletter.service.PayletterService;
import kr.co.winnticket.order.admin.dto.OrderAdminDetailGetResDto;
import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.shop.dto.OrderCreateReqDto;
import kr.co.winnticket.order.shop.dto.OrderCreateResDto;
import kr.co.winnticket.order.shop.dto.OrderShopGetResDto;
import kr.co.winnticket.order.shop.mapper.OrderShopMapper;
import kr.co.winnticket.product.admin.dto.ProductDetailGetResDto;
import kr.co.winnticket.product.admin.dto.ProductOptionGetResDto;
import kr.co.winnticket.product.admin.dto.ProductOptionValueGetResDto;
import kr.co.winnticket.product.admin.dto.ProductSmsTemplateDto;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import kr.co.winnticket.siteinfo.bankaccount.dto.BankAccountResponse;
import kr.co.winnticket.siteinfo.bankaccount.service.BankAccountService;
import kr.co.winnticket.sms.service.BizMsgService;
import kr.co.winnticket.sms.service.SmsTemplateFinder;
import kr.co.winnticket.sms.service.TemplateRenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderShopService {
    private final ProductMapper productMapper;
    private final OrderShopMapper mapper;
    private final ShopCartService shopCartService;
    private final ChannelMapper channelMapper;
    private final PayletterService paymentService;
    private final OrderMapper orderMapper;
    private final SmsTemplateFinder smsTemplateFinder;
    private final TemplateRenderService templateRenderService;
    private final BizMsgService bizMsgService;
    private final BankAccountService bankAccountService;
    // 주문조회
    public OrderShopGetResDto selectOrderShop(String orderNumber) {
        OrderShopGetResDto model = mapper.selectOrderShop(orderNumber);

        if (model == null) {
            throw new IllegalArgumentException("주문이 존재하지 않습니다.");
        }

        model.setProducts(mapper.selectOrderProductList(model.getId()));
        return model;
    }

    // 주문생성
    @Transactional
    public OrderCreateResDto createOrder(OrderCreateReqDto reqDto, HttpSession session) {
        try {
        log.info("createOrder start, channelId={}", reqDto.getChannelId());

        Boolean useCard = channelMapper.selectUseCardById(reqDto.getChannelId());
        Boolean cardAllowed = (useCard != null && useCard);

            // 결제수단 결정 (카드 미허용 채널이면 무조건 무통장으로 보정)
            PaymentMethod paymentMethod = reqDto.getPaymentMethod();
            if (!cardAllowed && paymentMethod == PaymentMethod.CARD) {
                paymentMethod = PaymentMethod.VIRTUAL_ACCOUNT;
            }

            // 주문 테이블 생성(입력한 정보들로)
        Map<String, Object> result = mapper.insertOrder(
            reqDto.getChannelId(),
            reqDto.getCustomerName(),
            reqDto.getCustomerPhone(),
            reqDto.getCustomerEmail(),
            reqDto.getTotalPrice(),
            reqDto.getDiscountPrice(),
                paymentMethod.name()
        );

        UUID orderId = (UUID) result.get("id");
        String orderNumber = (String) result.get("order_number");

        // 고른 각 상품들의 정보를 추출해서 각 상품의 정보를 추출
        for (OrderCreateReqDto.OrderItemReqDto item : reqDto.getItems()) {
            ProductDetailGetResDto product = productMapper.selectProductDetail(item.getProductId());

            if (product == null) {
                throw new IllegalArgumentException("상품이 존재하지 않습니다.");
            }

            // 주문한 상품 정보 insert
            UUID orderItemId = mapper.insertOrderItem(
                    orderId,
                    product.getId(),
                    product.getName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getTotalPrice(),
                    product.getPartnerId()
            );

            // 각 옵션 가격 추출해서 가격을 구한 후 검증을 추가해야함(api 구현하면서 추가할것)
            // 각 상품별 옵션 정보 insert
            if (item.getOptions() != null) {
                for (OrderCreateReqDto.OrderItemOptionReqDto opt : item.getOptions()) {
                    ProductOptionGetResDto option = productMapper.selectProductOptionDetail(opt.getOptionId());
                    ProductOptionValueGetResDto optionValue = productMapper.selectOptionValueDetail(opt.getOptionValueId());

                    if (option == null || optionValue == null) {
                        throw new IllegalArgumentException("유효하지 않은 옵션입니다.");
                    }

                    mapper.insertOrderItemOption (
                        orderItemId,
                        option.getName(),
                        optionValue.getValue(),
                        optionValue.getId(),
                        optionValue.getAdditionalPrice()
                    );
                }
            }
        }

        // 옵션값에 대한 계산 다시 실행 후 테이블에 넣어야함(api 연동할때) 최종결제금액 업데이트 지금은 프론트에서 주는대로 총 금액 - 할인금액
        int finalPrice = reqDto.getTotalPrice() - reqDto.getDiscountPrice();

        mapper.updateOrderPrice(orderId, finalPrice);

        // 주문성공 시 장바구니 비우기
        shopCartService.clearCart(session);

        OrderCreateResDto resDto = new OrderCreateResDto();
        resDto.setOrderId(orderId);
        // resDto.setPaymentStatus("READY");
        resDto.setOrderNumber(orderNumber);
        resDto.setFinalPrice(finalPrice);

        // 무통장(일반/베네피아 가능)
        if(paymentMethod == PaymentMethod.VIRTUAL_ACCOUNT){
            resDto.setPaymentStatus("READY");
            OrderAdminDetailGetResDto orderDetail =
                    orderMapper.selectOrderAdminDetail(orderId);

            List<OrderProductListGetResDto> items =
                    orderMapper.selectOrderProductList(orderId);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            try {
                                sendOrderReceiptSms(orderDetail, items);
                                log.info("무통장 주문접수 문자 발송 완료 - orderId={}", orderId);
                            } catch (Exception e) {
                                log.error("무통장 주문접수 문자 발송 실패 - orderId={}", orderId, e);
                            }
                        }
                    }
            );

            return resDto;
        }

            if (paymentMethod == PaymentMethod.CARD || paymentMethod == PaymentMethod.KAKAOPAY) {
//                String pgCode = String.valueOf(reqDto.getPaymentMethod());
//                if (pgCode == null || pgCode.isBlank()) {
//                    pgCode = "creditcard";
//                }
                String paymentMethodValue = paymentMethod.name();
                PayletterPaymentResDto payRes = paymentService.paymentRequest(
                        orderId,
                        orderNumber,
                        finalPrice,
                        reqDto.getCustomerName(),
                        reqDto.getCustomerEmail(),
                        reqDto.getCustomerPhone(),
                        paymentMethodValue
                );
            resDto.setPaymentStatus("REQUESTED");
            resDto.setPgProvider("PAYLETTER");
            resDto.setPgTid(String.valueOf(payRes.getToken()));
            resDto.setPgOnlineUrl(payRes.getOnlineUrl());
            resDto.setPgMobileUrl(payRes.getMobileUrl());
            return resDto;
        }
        return resDto;


        } catch (Exception e) {
            log.error("결재완료 중 오류 발생", e);
            throw e; // 다시 던짐 (중요)
        }
    }

    // 주문접수 문자 생성
    private void sendOrderReceiptSms(OrderAdminDetailGetResDto order,
                                     List<OrderProductListGetResDto> items) {

        if (order == null || items == null || items.isEmpty()) return;

        ProductSmsTemplateDto template;

        // 상품이 1개면 상품 템플릿
        if (items.size() == 1) {
            template = smsTemplateFinder.findTemplate(
                    items.get(0).getProductId(),
                    SmsTemplateCode.ORDER_RECEIVED
            );
        } else {
            // 여러 상품이면 기본 템플릿
            template = smsTemplateFinder.findDefaultTemplate(
                    SmsTemplateCode.ORDER_RECEIVED
            );
        }

        if (template == null || template.getContent() == null) {
            log.warn("주문접수 템플릿 없음 - orderId={}", order.getId());
            return;
        }

        // 2. 변수 구성
        Map<String, String> vars = new HashMap<>();

        vars.put("주문자명", order.getCustomerName());
        vars.put("주문번호", order.getOrderNumber());
        vars.put("상품목록", buildProductLines(items));
        vars.put("총결제금액", String.valueOf(order.getTotalPrice()));
        vars.put("입금계좌목록", buildAccountLines());

        // 3. 템플릿 치환
        String message = templateRenderService.render(template.getContent(), vars);

        // 4. CMID 생성
        String cmid = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20);

        // 5. 문자 발송
        bizMsgService.sendSms(
                cmid,
                order.getCustomerPhone(),
                order.getCustomerName(),
                "025118691",   // 발신번호
                "윈앤티켓",
                message
        );
    }

    // 상품 + 옵션 + 수량
    private String buildProductLines(List<OrderProductListGetResDto> items) {

        StringBuilder sb = new StringBuilder();

        for (OrderProductListGetResDto item : items) {

            String productName = item.getProductName();
            String optionText = buildOptionText(item);

            sb.append(productName);

            if (!optionText.isBlank()) {
                sb.append(" / ").append(optionText);
            }

            sb.append(" / ").append(item.getQuantity());
            sb.append("\n");
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); // 마지막 줄바꿈 제거
        }

        return sb.toString();
    }

    // 옵션 텍스트
    private String buildOptionText(OrderProductListGetResDto item) {
        if (item.getOptionName() != null) {
            return item.getOptionName();
        }

        return "";
    }

    // 계좌번호 목록
    private String buildAccountLines() {

        List<BankAccountResponse> accounts =
                bankAccountService.getVisibleBankAccounts();

        if (accounts == null || accounts.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        for (BankAccountResponse acc : accounts) {
            sb.append(acc.getBankName())
                    .append(" : ")
                    .append(acc.getAccountNumber())
                    .append("\n");
        }

        sb.setLength(sb.length() - 1);

        return sb.toString();
    }
}
