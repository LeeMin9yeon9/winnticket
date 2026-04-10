package kr.co.winnticket.order.admin.service;

import kr.co.winnticket.common.enums.SmsTemplateCode;
import kr.co.winnticket.integration.aquaplanet.service.AquaPlanetService;
import kr.co.winnticket.integration.benepia.order.service.BenepiaOrderService;
import kr.co.winnticket.integration.coreworks.service.CoreWorksService;
import kr.co.winnticket.integration.lscompany.service.LsCompanyService;
import kr.co.winnticket.integration.mair.service.MairService;
import kr.co.winnticket.integration.playstory.service.PlaystoryService;
import kr.co.winnticket.integration.plusn.service.PlusNService;
import kr.co.winnticket.integration.smartinfini.service.SmartInfiniService;
import kr.co.winnticket.integration.spavis.service.SpavisService;
import kr.co.winnticket.integration.woongjin.service.WoongjinService;
import kr.co.winnticket.order.admin.dto.*;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.product.admin.dto.ProductSmsTemplateDto;
import kr.co.winnticket.product.admin.mapper.ProductMapper;
import kr.co.winnticket.siteinfo.bankaccount.dto.BankAccountResDto;
import kr.co.winnticket.siteinfo.bankaccount.service.BankAccountService;
import kr.co.winnticket.siteinfo.companyinfo.dto.SiteInfoResponse;
import kr.co.winnticket.siteinfo.companyinfo.service.SiteInfoService;
import kr.co.winnticket.sms.service.BizMsgService;
import kr.co.winnticket.sms.service.SmsTemplateFinder;
import kr.co.winnticket.sms.service.TemplateRenderService;
import kr.co.winnticket.ticketCoupon.service.TicketCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 결제 후처리 헬퍼 서비스
 * - OrderService의 completePayment/cancelOrder 에서 위임받는 역할
 * - 파트너 발권 API 호출, SMS 발송, 티켓번호 생성 담당
 * - SMS는 BizMsgService가 @Async 처리하므로 비동기 발송
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPostPaymentService {

    private final OrderMapper mapper;
    private final ProductMapper productMapper;
    private final TicketCouponService ticketCouponService;
    private final BizMsgService bizMsgService;
    private final SmsTemplateFinder smsTemplateFinder;
    private final TemplateRenderService templateRenderService;
    private final SiteInfoService siteInfoService;

    // 파트너 서비스
    private final WoongjinService woongjinService;
    private final PlaystoryService playstoryService;
    private final MairService mairService;
    private final SmartInfiniService smartInfiniService;
    private final PlusNService plusNService;
    private final AquaPlanetService aquaplanetService;
    private final LsCompanyService lsCompanyService;
    private final BankAccountService bankAccountService;

    private static final String QR_URL = "https://www.winnticket.co.kr/qr?orderNumber=";
    private static final String BARCODE_URL = "https://www.winnticket.co.kr/barcode?orderNumber=";
    private static final String WOOGJIN = "bd0e1a6e-b871-44a0-827c-f44c0d82f3f4";
    private static final String PLAYSTORY = "e8e6f928-ebe2-44f9-930c-4a3f9a061b3c";
    private static final String MAIR = "15f283a9-fd6c-47ba-862d-0af9697a3e1b";
    private static final String COREWORKS = "1d5228eb-6d03-4e12-b370-b2ceb19a77cc";
    private static final String PLUSN = "85f50a52-7096-470e-95f5-a8e9c1cd6589";
    private static final String SMARTINFINI = "eec583a7-ce38-4cd0-927e-c35b5391a66d";
    private static final String SPAVIS = "0f46cad1-6fb4-4514-938f-d309850f0668";
    private static final String AQUAPLANET = "d16d7f6f-e432-40ee-9f57-e4aaa2c65751";
    private static final String LSCOMPANY = "b49be80d-4150-408b-80e6-e11c6f13db9d";

    // ─────────────────────────────────────────────
    // 티켓 발급 유틸
    // ─────────────────────────────────────────────

    public Boolean selectPrePurchased(UUID productId) {
        return productMapper.selectPrePurchasedByProductId(productId);
    }

    public String issueCoupon(UUID orderItemId, LocalDate validFrom, LocalDate validTo) {
        return ticketCouponService.issueCoupon(orderItemId, validFrom, validTo);
    }

    public String generateTicketNumber() {
        return "T"
                + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + UUID.randomUUID().toString().substring(0, 4);
    }

    // ─────────────────────────────────────────────
    // 파트너 발권 API
    // ─────────────────────────────────────────────

    /**
     * 파트너 API 호출
     * - 실패 시 RuntimeException 그대로 던짐
     * - 호출 측(OrderService.completePayment)의 트랜잭션 전체 롤백
     * - 파트너 발권 실패 = 결제도 함께 롤백 (소비자 보호)
     */
    public void callPartnerApis(UUID orderId, OrderAdminDetailGetResDto order, PartnerSplitResult split) {
        if (split.isHasWoongin()) {
            log.info("[Woongjin 발권]");
            woongjinService.order(orderId);
        }
        if (split.isHasPlaystory()) {
            log.info("[Playstory 발권]");
            playstoryService.order(orderId);
        }
        if (split.isHasMair()) {
            log.info("[Mair 발권]");
            mairService.issueTickets(order.getOrderNumber());
        }
        /*
        if (split.isHasCoreworks()) {
            log.info("[Coreworks 발권]");
            coreWorksService.order(orderId);
        }
        */
        if (split.isHasSmartInfini()) {
            log.info("[SmartInfini 발권]");
            smartInfiniService.order(orderId);
        }
        if (split.isHasPlusN()) {
            log.info("[PlusN 발권]");
            plusNService.order(orderId);
        }
        if (split.isHasLsCompany()) {
            log.info("[LSCompany 발권]");
            lsCompanyService.issueTicket(order.getId());
        }
        if (split.isHasAquaplanet()) {
            log.info("[Aquaplanet 발권]");
            aquaplanetService.issueOrder(orderId);
        }
    }

    // ─────────────────────────────────────────────
    // SMS 발송 (BizMsgService가 @Async → 비동기)
    // ─────────────────────────────────────────────

    /** 입금완료 문자 발송 */
    public void sendPaymentConfirmedSms(OrderAdminDetailGetResDto order, List<OrderProductListGetResDto> items) {
        if (items == null || items.isEmpty()) return;

        UUID productId = items.get(0).getProductId();
        ProductSmsTemplateDto template = smsTemplateFinder.findTemplate(productId, SmsTemplateCode.PAYMENT_CONFIRMED);
        if (template == null || template.getContent() == null) return;

        String message = templateRenderService.render(template.getContent(), Map.of(
                "주문자명", order.getCustomerName(),
                "주문번호", order.getOrderNumber(),
                "상품명", buildProductLines(items),
                "주문수량", String.valueOf(order.getAllCnt()),
                "주문금액", String.valueOf(order.getTotalPrice()),
                "입금계좌", buildAccountLines(),
                "고객센터", selectCallNumber()
        ));
        sendSms(order, message);
    }

    /** 발권완료 문자 발송 */
    public void sendTicketIssuedSms(OrderAdminDetailGetResDto order,
                                    List<OrderProductListGetResDto> items,
                                    Map<UUID, List<String>> ticketMap) {
        Set<String> sentProducts = new HashSet<>();

        for (OrderProductListGetResDto item : items) {
            UUID productId = item.getProductId();
            ProductSmsTemplateDto template = smsTemplateFinder.findTemplate(productId, SmsTemplateCode.TICKET_ISSUED);
            if (template == null || template.getContent() == null) continue;

            Map<String, String> vars = new HashMap<>();
            vars.put("주문자명", order.getCustomerName());
            vars.put("상품명", item.getProductName());
            vars.put("주문번호", order.getOrderNumber());
            vars.put("주문금액", String.valueOf(order.getTotalPrice()));
            vars.put("입금계좌", buildAccountLines());
            vars.put("고객센터", selectCallNumber());

            List<String> tickets = ticketMap.getOrDefault(item.getId(), new ArrayList<>());
            String ticketCodeType = mapper.selectTicketCodeType(item.getPartnerId());
            String couponText;

            if ("QR".equals(ticketCodeType)) {
                if (sentProducts.contains(String.valueOf(item.getPartnerId()))) continue;
                sentProducts.add(String.valueOf(item.getPartnerId()));
                couponText = QR_URL + order.getOrderNumber();
            } else if ("BARCODE".equals(ticketCodeType)) {
                if (sentProducts.contains(String.valueOf(item.getPartnerId()))) continue;
                sentProducts.add(String.valueOf(item.getPartnerId()));
                couponText = BARCODE_URL + order.getOrderNumber();
            } else {
                couponText = String.join("\n", tickets);
            }

            vars.put("티켓링크", couponText);
            vars.put("옵션명", item.getOptionName() == null ? "" : item.getOptionName());
            vars.put("주문수량", String.valueOf(item.getQuantity()));

            String message = templateRenderService.render(template.getContent(), vars);

            // 수령자 번호가 있으면 수령자에게, 없으면 주문자에게 발송
            sendCouponSms(order, message);
        }
    }

    /** 주문취소 문자 발송 */
    public void sendOrderCancelledSms(OrderAdminDetailGetResDto order, List<OrderProductListGetResDto> items) {
        if (order == null) return;

        UUID productId = items.get(0).getProductId();

        ProductSmsTemplateDto template = smsTemplateFinder.findTemplate(productId, SmsTemplateCode.ORDER_CANCELLED);
        if (template == null || template.getContent() == null) return;

        String message = templateRenderService.render(template.getContent(), Map.of(
                "주문자명", order.getCustomerName(),
                "주문번호", order.getOrderNumber(),
                "상품명", buildProductLines(items),
                "주문수량", String.valueOf(order.getAllCnt()),
                "주문금액", String.valueOf(order.getTotalPrice()),
                "입금계좌", buildAccountLines(),
                "고객센터", selectCallNumber()
        ));
        sendSms(order, message);
    }

    /** 발권 문자 재전송 (관리자용) */
    public void resendTicketSms(UUID orderId, OrderAdminDetailGetResDto order,
                                List<OrderProductListGetResDto> items,
                                Map<UUID, List<String>> ticketMap) {
        PartnerSplitResult split = splitByPartner(items);

        if (split.isHasSpavis() || split.isHasNormalProduct()
                || split.isHasSmartInfini() || split.isHasAquaplanet()) {
            List<OrderProductListGetResDto> normalItems = extractNormalProducts(items);
            sendTicketIssuedSms(order, normalItems, ticketMap);
        }
        /*else if(split.isHasCoreworks()) {
            log.info("[CoreWorks 문자재전송]");
            coreWorksService.order(orderId);
        }
         */
        else if(split.isHasLsCompany()) {
            log.info("[LSCompany 문자재전송]");
            lsCompanyService.resendTicket(order.getId());
        } else if(split.isHasWoongin()) {
            log.info("[Woongin 문자재전송]");
            woongjinService.resendPin(order.getId());
        }
    }

    // 문자 발송 공통부 (BizMsgService @Async → 비동기 발송)
    private void sendSms(OrderAdminDetailGetResDto order, String message) {
        String cmid = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        bizMsgService.sendSms(
                cmid,
                order.getCustomerPhone(),
                order.getCustomerName(),
                "025118691",
                "윈앤티켓",
                message
        );
    }

    // 쿠폰(QR/바코드) 문자 발송 - 수령자 번호 우선, 없으면 주문자 번호로 발송
    private void sendCouponSms(OrderAdminDetailGetResDto order, String message) {
        String phone = (order.getRecipientPhone() != null && !order.getRecipientPhone().isBlank())
                ? order.getRecipientPhone()
                : order.getCustomerPhone();
        String cmid = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        bizMsgService.sendSms(
                cmid,
                phone,
                order.getCustomerName(),
                "025118691",
                "윈앤티켓",
                message
        );
    }

    // 문자 변수 데이터 추출
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

        List<BankAccountResDto> accounts =
                bankAccountService.getVisibleBankAccounts();

        if (accounts == null || accounts.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        for (BankAccountResDto acc : accounts) {
            sb.append(acc.getBankName())
                    .append(" : ")
                    .append(acc.getAccountNumber())
                    .append("\n");
        }

        sb.setLength(sb.length() - 1);

        return sb.toString();
    }

    // 고객센터
    private String selectCallNumber() {
        SiteInfoResponse siteInfo = siteInfoService.getSiteInfo();

        if (siteInfo == null) return "";

        return siteInfo.getCustomerServiceTel();
    }

    // ─────────────────────────────────────────────
    // 파트너 분기 유틸
    // ─────────────────────────────────────────────

    public PartnerSplitResult splitByPartner(List<OrderProductListGetResDto> items) {
        boolean hasWoongin = false, hasPlaystory = false, hasMair = false;
        boolean hasCoreworks = false, hasSmartInfini = false, hasPlusN = false;
        boolean hasAquaplanet = false, hasSpavis = false, hasLsCompany = false;
        boolean hasNormalProduct = false;

        for (OrderProductListGetResDto item : items) {
            String partnerId = String.valueOf(item.getPartnerId());

            if (WOOGJIN.equals(partnerId)) hasWoongin = true;
            else if (PLAYSTORY.equals(partnerId)) hasPlaystory = true;
            else if (MAIR.equals(partnerId)) hasMair = true;
            else if (COREWORKS.equals(partnerId)) hasCoreworks = true;
            else if (SMARTINFINI.equals(partnerId)) hasSmartInfini = true;
            else if (PLUSN.equals(partnerId)) hasPlusN = true;
            else if (AQUAPLANET.equals(partnerId)) hasAquaplanet = true;
            else if (SPAVIS.equals(partnerId)) hasSpavis = true;
            else if (LSCOMPANY.equals(partnerId)) hasLsCompany = true;
            else hasNormalProduct = true;
        }

        return new PartnerSplitResult(
                hasWoongin, hasPlaystory, hasMair, hasCoreworks,
                hasSmartInfini, hasPlusN, hasAquaplanet, hasSpavis,
                hasLsCompany, hasNormalProduct
        );
    }

    public List<OrderProductListGetResDto> extractNormalProducts(List<OrderProductListGetResDto> items) {
        return items.stream()
                .filter(item -> {
                    String partnerId = String.valueOf(item.getPartnerId());
                    return partnerId == null
                            || (!WOOGJIN.equals(partnerId)
                            && !PLAYSTORY.equals(partnerId)
                            && !MAIR.equals(partnerId)
                            //&& !COREWORKS.equals(partnerId)
                            && !PLUSN.equals(partnerId)
                            && !LSCOMPANY.equals(partnerId));
                })
                .toList();
    }
}
