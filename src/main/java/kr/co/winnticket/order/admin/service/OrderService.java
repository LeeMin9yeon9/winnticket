package kr.co.winnticket.order.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kr.co.winnticket.common.enums.PaymentMethod;
import kr.co.winnticket.common.enums.PaymentStatus;
import kr.co.winnticket.common.enums.SmsTemplateCode;
import kr.co.winnticket.integration.aquaplanet.service.AquaplanetService;
import kr.co.winnticket.integration.coreworks.service.CoreWorksService;
import kr.co.winnticket.integration.mair.service.MairService;
import kr.co.winnticket.integration.payletter.dto.PayletterCancelResDto;
import kr.co.winnticket.integration.payletter.service.PayletterService;
import kr.co.winnticket.integration.playstory.service.PlaystoryService;
import kr.co.winnticket.integration.plusn.service.PlusNService;
import kr.co.winnticket.integration.smartinfini.service.SmartInfiniService;
import kr.co.winnticket.integration.spavis.service.SpavisService;
import kr.co.winnticket.integration.woongjin.service.WoongjinService;
import kr.co.winnticket.order.admin.dto.*;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.product.admin.dto.ProductSmsTemplateDto;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import kr.co.winnticket.sms.service.BizMsgService;
import kr.co.winnticket.sms.service.SmsTemplateFinder;
import kr.co.winnticket.sms.service.TemplateRenderService;
import kr.co.winnticket.ticketCoupon.service.TicketCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper mapper;
    private final SmsTemplateFinder smsTemplateFinder;
    private final TemplateRenderService templateRenderService;
    private final BizMsgService bizMsgService;
    private final PayletterService payletterService;
    private final ObjectMapper objectMapper;
    private final TicketCouponService ticketCouponService;
    private final ProductMapper productMapper;

    // 파트너 연동
    private final WoongjinService woongjinService;
    private final PlaystoryService playstoryService;
    private final MairService mairService;
    private final CoreWorksService coreWorksService;
    private final SmartInfiniService smartInfiniService;
    private final PlusNService plusNService;
    private final AquaplanetService aquaplanetService;
    private final SpavisService spavisService;

    // 주문 상태 조회
    public OrderAdminStatusGetResDto selectOrderAdminStatus() {
        OrderAdminStatusGetResDto model = mapper.selectOrderAdminStatus();
        return model;
    }

    // 주문 목록 조회 (관리자)
    public List<OrderAdminListGetResDto> selectOrderAdminList(String asSrchWord, LocalDate asBegDate, LocalDate asEndDate, String status) {
        List<OrderAdminListGetResDto> lModel = mapper.selectOrderAdminList(asSrchWord, asBegDate, asEndDate, status);
        return lModel;
    }

    // 주문 상세 조회 (관리자)
    public OrderAdminDetailGetResDto selectOrderAdminDetail(UUID auId) {
        OrderAdminDetailGetResDto model = mapper.selectOrderAdminDetail(auId);
        model.setProducts(mapper.selectOrderProductList(auId));
        model.setTickets(mapper.selectOrderTicketList(auId));
        return model;
    }

    // 티켓조회(현장관리자)
    public OrderAdminTicketCheckGetResDto selectOrderAdminTicketList(UUID auId) {
        OrderAdminTicketCheckGetResDto model = mapper.selectOrderTicketHeader(auId);
        List<OrderTicketDetailGetResDto> tickets = mapper.selectOrderTickets(auId);

        model.setTickets(tickets);

        int total = tickets.size();
        int used = (int) tickets.stream()
                .filter(OrderTicketDetailGetResDto::isTicketUsed)
                .count();

        model.setTotalTicketCnt(total);
        model.setUsedTicketCnt(used);
        model.setUnusedTicketCnt(total - used);

        return model;
    }

    // 결제 완료 처리
    @Transactional
    public void completePayment(UUID auId) {
        try {
            // 주문 조회
            OrderAdminDetailGetResDto order = mapper.selectOrderAdminDetail(auId);

            if (order == null) {
                throw new IllegalArgumentException("주문이 존재하지 않습니다.");
            }

            if (order.getPaymentStatus() == PaymentStatus.PAID) {
                log.info("이미 결제 완료 → completePayment skip. orderId={}", auId);
                return;
            }

            // 결제 상태 / 결제일시 업데이트
            log.info("[결제일시 업데이트 시작!]");
            mapper.updatePaymentComplete(auId, LocalDateTime.now());
            log.info("[결제일시 업데이트 종료!]");
            // 주문 상품 목록 조회
            List<OrderProductListGetResDto> items = mapper.selectOrderProductList(auId);

            log.info("[입금완료 문자 발송 시작!]");
            sendOrderSmsByStatus(order, items, SmsTemplateCode.PAYMENT_CONFIRMED);
            log.info("[입금완료 문자 발송 종료!]");

            // 티켓 발행
            for (OrderProductListGetResDto item : items) {
                UUID productId = item.getProductId();
                log.info("productId = {}", productId);
                Boolean prePurchased = productMapper.selectPrePurchasedByProductId(productId);
                log.info("[선사입이야?] = {}", prePurchased);
                for (int i = 0; i < item.getQuantity(); i++) {
                    // 선사입쿠폰
                    if(Boolean.TRUE.equals(prePurchased)){
                        log.info("[응 선사입]");
                        ticketCouponService.issueCoupon(item.getId());
                    }else {
                        log.info("[아니 선사입아냐]");
                        mapper.insertOrderTicket(
                                item.getId(),   // orderItemId
                                generateTicketNumber(auId, item.getId())
                        );
                    }
                }
            }

            // 주문 상태 변경
            log.info("[주문상태 변경 시작!]");
            mapper.updateOrderStatus(auId);
            log.info("[주문상태 변경 종료!]");

            PartnerSplitResult split = splitByPartner(items);

            log.info("split = {}", split);
            if (split.isHasWoongin()) {
                log.info("[웅진 상품이래요!]");
                woongjinService.order(auId);
                log.info("[웅진 결과!] = {}", woongjinService.order(auId));
            }

            if (split.isHasPlaystory()) {
                log.info("[플레이스토리 상품이래요!]");
                playstoryService.order(auId);
                log.info("[플레이스토리 결과!] = {}", playstoryService.order(auId));
            }

            if (split.isHasMair()) {
                log.info("[엠에어 상품이래요!]");
                mairService.issueTickets(order.getOrderNumber());
                log.info("[엠에어 결과!] = {}", mairService.issueTickets(order.getOrderNumber()));
            }

            if (split.isHasCoreworks()) {
                log.info("[코어웍스 상품이래요!]");
                coreWorksService.order(auId);
                log.info("[코어웍스 결과!] = {}", coreWorksService.order(auId));
            }

            if (split.isHasSmartInfini()) {
                log.info("[스마트인피니 상품이래요!]");
                smartInfiniService.order(auId);
                log.info("[스마트인피니 결과!] = {}", smartInfiniService.order(auId));
            }

            if (split.isHasPlusN()) {
                log.info("[플러스앤 상품이래요!]");
                plusNService.order(auId);
                log.info("[플러스앤 결과!] = {}", plusNService.order(auId));
            }

            /*
            if (split.isHasAquaplanet()) {
                aquaplanetService.couponIssue(auId);
                continue;
            }
             */

            if (split.isHasSpavis() || split.isHasNormalProduct()) {
                log.info("[자체 상품이래요!]");
                List<OrderProductListGetResDto> normalItems = extractNormalProducts(items);
                log.info("[발권 문자 발송 시작]");
                sendOrderSmsByStatus(order, normalItems, SmsTemplateCode.TICKET_ISSUED);
                log.info("[발권 문자 발송 종료]");
            }
        } catch (Exception e) {
            log.error("주문 생성 중 오류 발생", e);
            throw e; // 다시 던짐 (중요)
        }
    }

    // 상품 분기 처리
    private PartnerSplitResult splitByPartner(List<OrderProductListGetResDto> items) {
        boolean hasWoongin = false;
        boolean hasPlaystory = false;
        boolean hasMair = false;
        boolean hasCoreworks = false;
        boolean hasSmartInfini = false;
        boolean hasPlusN = false;
        boolean hasAquaplanet = false;
        boolean hasSpavis = false;
        boolean hasNormalProduct = false;

        for (OrderProductListGetResDto item : items) {
            String partnerId = String.valueOf(item.getPartnerId());

            log.error("partnerId = {}", partnerId);

            // 파트너별 상품이 있는지 체크
            if ("bd0e1a6e-b871-44a0-827c-f44c0d82f3f4".equals(partnerId)) { // 웅진컴퍼스
                hasWoongin = true;
            } else if("e8e6f928-ebe2-44f9-930c-4a3f9a061b3c".equals(partnerId)) { // 플레이스토리
                hasPlaystory = true;
            } else if("15f283a9-fd6c-47ba-862d-0af9697a3e1b".equals(partnerId)) {// 엠에어
                hasMair = true;
            } else if("1d5228eb-6d03-4e12-b370-b2ceb19a77cc".equals(partnerId)) { // 코어웍스
                hasCoreworks = true;
            } else if("eec583a7-ce38-4cd0-927e-c35b5391a66d".equals(partnerId)) { // 스마트인피니
                hasSmartInfini = true;
            } else if("85f50a52-7096-470e-95f5-a8e9c1cd6589".equals(partnerId)) { // 플러스앤
                hasPlusN = true;
            } else if("d16d7f6f-e432-40ee-9f57-e4aaa2c65751".equals(partnerId)) { // 아쿠아플래닛
                hasAquaplanet = true;
            } else if("0f46cad1-6fb4-4514-938f-d309850f0668".equals(partnerId)) { // 스파비스
                hasSpavis = true;
            } else { // 일반상품
                hasNormalProduct = true;
            }
        }

        return new PartnerSplitResult(
                hasWoongin,
                hasPlaystory,
                hasMair,
                hasCoreworks,
                hasSmartInfini,
                hasPlusN,
                hasAquaplanet,
                hasSpavis,
                hasNormalProduct
        );
    }

    // 발권 문자 발송 대상 뽑기 (자체 문자 발송하는 경우만 추출)
    private List<OrderProductListGetResDto> extractNormalProducts(List<OrderProductListGetResDto> items
    ) {
        return items.stream()
                .filter(item -> {
                    String partnerId = String.valueOf(item.getPartnerId());

                    return partnerId == null
                            || (!"bd0e1a6e-b871-44a0-827c-f44c0d82f3f4".equals(partnerId)
                            && !"e8e6f928-ebe2-44f9-930c-4a3f9a061b3c".equals(partnerId)
                            && !"15f283a9-fd6c-47ba-862d-0af9697a3e1b".equals(partnerId)
                            && !"1d5228eb-6d03-4e12-b370-b2ceb19a77cc".equals(partnerId)
                            && !"eec583a7-ce38-4cd0-927e-c35b5391a66d".equals(partnerId)
                            && !"85f50a52-7096-470e-95f5-a8e9c1cd6589".equals(partnerId)
                            && !"d16d7f6f-e432-40ee-9f57-e4aaa2c65751".equals(partnerId));
                })
                .toList();
    }

    // 문자 발송
    private void sendOrderSmsByStatus(
            OrderAdminDetailGetResDto order,
            List<OrderProductListGetResDto> items,
            SmsTemplateCode templateCode
    ) {

        if (items == null || items.isEmpty()) return;

        // 입금완료는 주문당 1번만 발송
        if (templateCode == SmsTemplateCode.PAYMENT_CONFIRMED) {

            UUID productId = items.get(0).getProductId();

            ProductSmsTemplateDto template = smsTemplateFinder.findTemplate(productId, templateCode);

            if (template == null || template.getContent() == null) return;

            String message =
                    templateRenderService.render(template.getContent(), Map.of(
                            "주문자명", order.getCustomerName(),
                            "주문번호", order.getOrderNumber()
                    ));

            sendSms(order, message);
            return;
        } else if(templateCode == SmsTemplateCode.TICKET_ISSUED) {
            // 발권완료 상품별 반복
            for (OrderProductListGetResDto item : items) {

                UUID productId = item.getProductId();

                ProductSmsTemplateDto template = smsTemplateFinder.findTemplate(productId, templateCode);

                if (template == null || template.getContent() == null) continue;

                Map<String, String> vars = new HashMap<>();
                vars.put("주문자명", order.getCustomerName());
                vars.put("상품명", item.getProductName());
                vars.put("주문번호", order.getOrderNumber());
                vars.put("옵션값명",
                        item.getOptionName() == null ? "" : item.getOptionName());
                vars.put("수량", String.valueOf(item.getQuantity()));

                String message = templateRenderService.render(template.getContent(), vars);

                log.error("발권문자 왜 아노아", message);
                sendSms(order, message);
            }
        }
    }

    // 문자 발송 공통부
    private void sendSms(OrderAdminDetailGetResDto order, String message) {

        String cmid = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20);

        bizMsgService.sendSms(
                cmid,
                order.getCustomerPhone(),
                order.getCustomerName(),
                "025118691",
                "윈앤티켓",
                message
        );
    }

    // 티켓번호 생성
    private String generateTicketNumber(UUID auId, UUID orderItemId) {
        return "T-"
                + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-"
                + UUID.randomUUID().toString().substring(0, 8);
    }

    // 티켓 사용 처리
    @Transactional
    public void useTicket(UUID orderId, UUID ticketId) {
        // 티켓 사용 처리
        int updated = mapper.updateTicketUsed(ticketId);

        if (updated == 0) {
            throw new IllegalStateException("이미 사용된 티켓이거나 존재하지 않습니다.");
        }

        // 주문 내 미사용 티켓 존재 여부 확인
        int remainCount = mapper.countUnusedTickets(orderId);

        // 전부 사용됐으면 주문 상태 변경
        if (remainCount == 0) {
            mapper.updateOrderCompleted(orderId);
        }
    }

    // 주문 취소
    @Transactional
    public void cancelOrder(UUID orderId) throws JsonProcessingException{

        // 주문 조회
        OrderAdminDetailGetResDto order = mapper.selectOrderAdminDetail(orderId);
        if(order == null){
            throw new IllegalArgumentException("주문 정보가 존재하지 않습니다.");
        }

        if (order.getPaymentStatus() == PaymentStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }

        // 결제 완료만 취소
        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("결제 완료된 주문만 취소할 수 있습니다.");
        }

        // 사용된 티켓 확인
        int usedTicketCount = mapper.countUsedTickets(orderId);
        if (usedTicketCount > 0) {
            throw new IllegalStateException("사용된 티켓이 포함된 주문은 취소할 수 없습니다.");
        }
        // 2) 결제수단 분기
        PaymentMethod method = order.getPaymentMethod();

        PayletterCancelResDto res = null;

        if (method == PaymentMethod.VIRTUAL_ACCOUNT) {
            //cancelVirtualAccount(order);
        } else if (method == PaymentMethod.CARD) {
           res =  payletterService.cancel(orderId);
        } else {
            throw new IllegalArgumentException("지원하지 않는 결제수단입니다. method=" + method);
        }

        String payloadJson = objectMapper.writeValueAsString(res);
        int updated = mapper.updatePayletterCancelSuccess(orderId, payloadJson);


       if(updated != 1){
           throw new IllegalStateException("주문 취소 상태 변경 실패");
       }

        log.info("[ORDER_CANCEL] 관리자 취소 완료 orderId={}, paymentMethod={}", orderId, method);

    }
}

