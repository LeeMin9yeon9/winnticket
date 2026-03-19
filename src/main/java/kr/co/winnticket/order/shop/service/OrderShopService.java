package kr.co.winnticket.order.shop.service;

import jakarta.servlet.http.HttpSession;
import kr.co.winnticket.cart.service.ShopCartService;
import kr.co.winnticket.channels.channel.mapper.ChannelMapper;
import kr.co.winnticket.common.enums.PaymentMethod;
import kr.co.winnticket.common.enums.ProductType;
import kr.co.winnticket.common.enums.SmsTemplateCode;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointCancelReqDto;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointPayReqDto;
import kr.co.winnticket.integration.benepia.kcp.service.KcpService;
import kr.co.winnticket.integration.benepia.sso.context.BenepiaContext;
import kr.co.winnticket.integration.benepia.sso.dto.BenepiaDecryptedParamDto;
import kr.co.winnticket.integration.payletter.dto.PayletterPaymentResDto;
import kr.co.winnticket.integration.payletter.service.PayletterService;
import kr.co.winnticket.order.admin.dto.OrderAdminDetailGetResDto;
import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.admin.service.OrderService;
import kr.co.winnticket.order.shop.dto.OrderCreateReqDto;
import kr.co.winnticket.order.shop.dto.OrderCreateResDto;
import kr.co.winnticket.order.shop.dto.OrderQrCouponGetResDto;
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
    private final PriceService priceService;
    private final KcpService kcpService;
    private final OrderService orderService;
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

        log.info("createOrder start, channelId={}", reqDto.getChannelId());
        log.info(" paymentMethod = {}", reqDto.getPaymentMethod());
        log.info(" pointAmount(raw) = {}", reqDto.getPointAmount());

        Boolean useCard = channelMapper.selectUseCardById(reqDto.getChannelId());
        Boolean cardAllowed = (useCard != null && useCard);

        Boolean usePoint = channelMapper.selectUsePointById(reqDto.getChannelId());
        log.info("usePoint(DB) = {}", usePoint);

        // 결제수단 결정 (카드 미허용 채널이면 무조건 무통장으로 보정)
        PaymentMethod paymentMethod = reqDto.getPaymentMethod();
        // 카드 미허용 채널이면 카드 -> 무통장
        if (!cardAllowed && paymentMethod == PaymentMethod.CARD) {
            paymentMethod = PaymentMethod.VIRTUAL_ACCOUNT;
        }

        // 포인트 미허용 채널이면 차단
        Integer pointAmount = reqDto.getPointAmount() == null ? 0 : reqDto.getPointAmount();

        boolean isPointUsed = pointAmount > 0 || paymentMethod == PaymentMethod.POINT;

        log.info("pointAmount(계산) = {}", pointAmount);
        log.info("isPointUsed = {}", isPointUsed);


        if (isPointUsed && !Boolean.TRUE.equals(usePoint)) {
            throw new IllegalArgumentException("해당 채널에서는 포인트 결제가 불가능합니다.");
        }

        BenepiaDecryptedParamDto bene = BenepiaContext.get();

        // 주문 테이블 생성(입력한 정보들로)
        Map<String, Object> result = mapper.insertOrder(
                reqDto.getChannelId(),
                reqDto.getCustomerName(),
                reqDto.getCustomerPhone(),
                reqDto.getCustomerEmail(),
                reqDto.getTotalPrice(),
                reqDto.getDiscountPrice(),
                paymentMethod.name(),
                bene.getBenefit_id()
        );

        UUID orderId = (UUID) result.get("id");
        String orderNumber = (String) result.get("order_number");

        int orderTotalPrice = 0;

        // 고른 각 상품들의 정보를 추출해서 각 상품의 정보를 추출
        for (OrderCreateReqDto.OrderItemReqDto item : reqDto.getItems()) {
            ProductDetailGetResDto product = productMapper.selectProductDetail(item.getProductId());

            if (product == null) {
                throw new IllegalArgumentException("상품이 존재하지 않습니다.");
            }
            // 가격 계산
            int unitPrice;

            if (ProductType.STAY.equals(product.getType())) {
                unitPrice = priceService.calculateStayUnitPrice(item.getOptions(), item.getStayDates());
            } else {
                unitPrice = priceService.calculateNormalPrice(item.getProductId(), reqDto.getChannelId(), item.getOptions());
            }

            int totalPrice = unitPrice * item.getQuantity();
            log.info("====가격 - totalPrice={}", totalPrice);
            orderTotalPrice += totalPrice;

            // 가격 위변조 체크
            if (item.getUnitPrice() != unitPrice) {
                log.info("====프론트가격 - getUnitPrice={}, 계산가격 ={}", item.getUnitPrice(), unitPrice);
                throw new IllegalArgumentException("가격이 변경되었습니다.");
            }

            // 주문한 상품 정보 insert
            UUID orderItemId = mapper.insertOrderItem(
                    orderId,
                    product.getId(),
                    product.getName(),
                    item.getQuantity(),
                    unitPrice,
                    totalPrice,
                    product.getPartnerId()
            );

            // 각 상품별 옵션 정보 insert
            if (item.getOptions() != null) {
                for (OrderCreateReqDto.OrderItemOptionReqDto opt : item.getOptions()) {
                    ProductOptionGetResDto option = productMapper.selectProductOptionDetail(opt.getOptionId());
                    ProductOptionValueGetResDto optionValue = productMapper.selectOptionValueDetail(opt.getOptionValueId());

                    if (option == null || optionValue == null) {
                        throw new IllegalArgumentException("유효하지 않은 옵션입니다.");
                    }

                    if (!ProductType.STAY.equals(product.getType())
                            && optionValue.getStock() < item.getQuantity()) {
                        throw new IllegalArgumentException("재고가 부족합니다.");
                    }

                    mapper.insertOrderItemOption(
                            orderItemId,
                            option.getName(),
                            optionValue.getValue(),
                            optionValue.getId(),
                            optionValue.getAdditionalPrice()
                    );
                }
            }
        }

        // 최종결제금액 업데이트 총 금액 - 할인금액
        int finalPrice = orderTotalPrice - reqDto.getDiscountPrice();

        log.info("====토탈가격 - orderTotalPrice={}", orderTotalPrice);
        log.info("====최종가격 - finalPrice={}", finalPrice);

        // 최종 결제금액 - 포인트 금액
        int pgAmount = finalPrice - pointAmount;

        if (pointAmount > finalPrice) {
            throw new IllegalArgumentException("포인트가 결제금액보다 큽니다.");
        }

        if (pgAmount < 0) {
            throw new IllegalArgumentException("포인트 금액 오류");
        }

        mapper.updateOrderPrice(orderId, finalPrice, pointAmount);

        log.info("결제금액 구조 finalPrice={}, pointAmount={}, pgAmount={}", finalPrice, pointAmount, pgAmount);

        OrderCreateResDto resDto = new OrderCreateResDto();
        resDto.setOrderId(orderId);
        // resDto.setPaymentStatus("READY");
        resDto.setOrderNumber(orderNumber);
        resDto.setFinalPrice(finalPrice);

        // 주문성공 시 장바구니 비우기
        shopCartService.clearCart(session);

        // 무통장(일반/베네피아 가능)
        if (paymentMethod == PaymentMethod.VIRTUAL_ACCOUNT) {
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
        if (paymentMethod == PaymentMethod.POINT) {

            String benepiaId = reqDto.getBenepiaId();
            String benepiaPwd = reqDto.getBenepiaPwd();

            if (benepiaId == null || benepiaPwd == null) {
                throw new IllegalArgumentException("베네피아 ID/PW 필요");
            }

            List<OrderProductListGetResDto> items =
                    orderMapper.selectOrderProductList(orderId);

            KcpPointPayReqDto dto = new KcpPointPayReqDto();

            dto.setOrderNo(orderNumber);
            dto.setAmount(pointAmount);

            dto.setProductName(items.get(0).getProductName());
            dto.setProductCode(items.get(0).getProductCode());

            dto.setBuyerName(reqDto.getCustomerName());
            dto.setBuyerEmail(reqDto.getCustomerEmail());
            dto.setBuyerPhone(reqDto.getCustomerPhone());

            dto.setBenepiaId(benepiaId);
            dto.setBenepiaPwd(benepiaPwd);

            try {

                kcpService.pointPayAndUpdate(dto);

                log.info("[POINT] 포인트 단독결제 성공 orderId={}", orderId);

                // ⭐ 반드시 결제완료 로직 실행
                orderService.completePayment(orderId);

            } catch (Exception e) {

                log.error("[POINT] 포인트 결제 실패", e);

                throw new RuntimeException("포인트 결제 실패");
            }

            resDto.setPaymentStatus("PAID");

            return resDto;
        }
        // POINT
        // 혼합결제 포인트 먼저 차감

        boolean pointDeducted = false;

        if (pointAmount > 0) {

            String benepiaId = reqDto.getBenepiaId();
            String benepiaPwd = reqDto.getBenepiaPwd();

            if (benepiaId == null || benepiaPwd == null) {
                throw new IllegalArgumentException("베네피아 ID/PW 필요");
            }

            List<OrderProductListGetResDto> items = orderMapper.selectOrderProductList(orderId);

            KcpPointPayReqDto dto = new KcpPointPayReqDto();

            dto.setOrderNo(orderNumber);
            dto.setAmount(pointAmount);

            dto.setProductName(items.get(0).getProductName());
            dto.setProductCode(items.get(0).getProductCode());

            dto.setBuyerName(reqDto.getCustomerName());
            dto.setBuyerEmail(reqDto.getCustomerEmail());
            dto.setBuyerPhone(reqDto.getCustomerPhone());

            dto.setBenepiaId(benepiaId);
            dto.setBenepiaPwd(benepiaPwd);

            try {

                kcpService.pointPayAndUpdate(dto);
                pointDeducted = true;

                log.info("혼합결제 포인트 선차감 완료 orderId={}", orderId);

            } catch (Exception e) {

                log.error("포인트 결제 실패", e);

                throw new RuntimeException("포인트 결제 실패");
            }
        }

        //CARD
        if (paymentMethod == PaymentMethod.CARD || paymentMethod == PaymentMethod.KAKAOPAY) {
            try {


                // PG 결제 요청
                PayletterPaymentResDto payRes = paymentService.paymentRequest(
                        orderId,
                        orderNumber,
                        pgAmount,
                        reqDto.getCustomerName(),
                        reqDto.getCustomerEmail(),
                        reqDto.getCustomerPhone(),
                        paymentMethod.name()
                );

                resDto.setPaymentStatus("REQUESTED");
                resDto.setPgProvider("PAYLETTER");
                resDto.setPgTid(String.valueOf(payRes.getToken()));
                resDto.setPgOnlineUrl(payRes.getOnlineUrl());
                resDto.setPgMobileUrl(payRes.getMobileUrl());

                return resDto;
            } catch (Exception pgError) {
                log.error("[PG ERROR] 결제요청 실패 orderId={}", orderId, pgError);

                if(pointDeducted){
                    try {
                        String tno = orderMapper.selectPointTno(orderNumber);
                        if(tno != null){
                            KcpPointCancelReqDto cancelReqDto = new KcpPointCancelReqDto();

                            cancelReqDto.setTno(tno);
                            cancelReqDto.setCancelReason("PG 결제  실패 롤백");

                            kcpService.cancelPoint(cancelReqDto);

                            log.info("[POINT ROLLBACK] 포인트 자동복구 orderId={}", orderId);
                        }
                    }catch (Exception rollbackError){
                        log.error("[POINT ROLLBACK FAIL] 관리자 확인 필요 orderId={}",orderId,rollbackError);
                    }
                }
                throw new RuntimeException("PG 결제 요청 실패");
            }
        }
        return resDto;
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

    // QR 생성
    public OrderQrCouponGetResDto getQrCoupon(String orderNumber) {

        // 주문 기본 정보
        OrderQrCouponGetResDto res = mapper.selectOrderQrInfo(orderNumber);

        if (res == null) {
            throw new IllegalArgumentException("쿠폰이 존재하지 않습니다.");
        }

        // 주문에 속한 티켓 목록 조회
        List<OrderQrCouponGetResDto.Ticket> tickets = mapper.selectTicketsByOrderNumber(orderNumber);

        if (tickets == null || tickets.isEmpty()) {
            throw new IllegalArgumentException("티켓이 존재하지 않습니다.");
        }

        // 파트너별 QR 값 설정
        for (OrderQrCouponGetResDto.Ticket ticket : tickets) {

            String partnerId = res.getPartnerId();

            // 스파비스
            if ("0f46cad1-6fb4-4514-938f-d309850f0668".equals(partnerId)) {
                ticket.setQrValue(ticket.getTicketNumber());
            }

            // 서울랜드
            else if ("eec583a7-ce38-4cd0-927e-c35b5391a66d".equals(partnerId)) {
                ticket.setQrValue(ticket.getPartnerOrderCode());
            } else {
                throw new IllegalArgumentException("QR 쿠폰이 없는 상품입니다.");
            }
        }

        res.setTickets(tickets);

        return res;
    }
}
