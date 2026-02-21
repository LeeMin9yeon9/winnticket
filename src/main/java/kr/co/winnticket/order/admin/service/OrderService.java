package kr.co.winnticket.order.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kr.co.winnticket.common.enums.OrderStatus;
import kr.co.winnticket.common.enums.PaymentMethod;
import kr.co.winnticket.common.enums.PaymentStatus;
import kr.co.winnticket.common.enums.SmsTemplateCode;
import kr.co.winnticket.integration.payletter.dto.PayletterCancelResDto;
import kr.co.winnticket.integration.payletter.service.PayletterService;
import kr.co.winnticket.order.admin.dto.*;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.order.admin.mapper.OrderStatusSmsMapper;
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
            mapper.updatePaymentComplete(auId, LocalDateTime.now());

            // 주문 상품 목록 조회
            List<OrderProductListGetResDto> items = mapper.selectOrderProductList(auId);

            // 티켓 발행
           // for (OrderProductListGetResDto item : items) { for (int i = 0; i < item.getQuantity(); i++) { mapper.insertOrderTicket(auId, item.getId(), generateTicketNumber(auId, item.getId())); }
            for (OrderProductListGetResDto item : items) {

                UUID productId = item.getProductId();
                Boolean prePurchased = productMapper.selectPrePurchasedByProductId(productId);

                for (int i = 0; i < item.getQuantity(); i++) {
                    // 선사입쿠폰
                    if(Boolean.TRUE.equals(prePurchased)){
                        ticketCouponService.issueCoupon(item.getId());

                    }else {
                        mapper.insertOrderTicket(
                                item.getId(),   // orderItemId
                                generateTicketNumber(auId, item.getId())
                        );
                    }
                }
            }

            // 주문 상태 변경
            mapper.updateOrderStatus(auId);

            // 문자 발송 (결제완료)
            sendOrderSmsByStatus(order, items, OrderStatus.COMPLETED);
        } catch (Exception e) {
            log.error("주문 생성 중 오류 발생", e);
            throw e; // 다시 던짐 (중요)
        }
    }

    // 문자 발송
    private void sendOrderSmsByStatus(OrderAdminDetailGetResDto order,
                                      List<OrderProductListGetResDto> items,
                                      OrderStatus status) {

        List<SmsTemplateCode> codes =
                OrderStatusSmsMapper.map(status);

        if (codes.isEmpty()) return;

        // 대표상품 1개 기준
        OrderProductListGetResDto first = items.get(0);
        UUID productId = first.getProductId();

        for (SmsTemplateCode code : codes) {

            // 1. 템플릿 조회
            ProductSmsTemplateDto template =
                    smsTemplateFinder.findTemplate(productId, code);

            if (template == null || template.getContent() == null) continue;

            // 2. 변수 구성
            Map<String,String> vars = new HashMap<>();
            vars.put("상품명", first.getProductName());                 // DTO 필드명에 맞춰 수정
            vars.put("주문번호", order.getOrderNumber());
            vars.put("주문자명", order.getCustomerName());
            vars.put("주문수량", String.valueOf(first.getQuantity()));
            vars.put("주문금액", String.valueOf(order.getTotalPrice())); // 포맷 필요하면 format
            vars.put("입금계좌", "국민은행\t123-456-789012\t(주)티켓박스");     // 없으면 공통값/설정값 사용
            vars.put("티켓링크", "https://winnticket.store/ticket/" + order.getOrderNumber());         // 아래 함수 예시
            vars.put("고객센터", "1588-1234");

            // 3. 템플릿 치환
            String message =
                    templateRenderService.render(template.getContent(), vars);

            // 4. CMID 생성 (중복 방지)
            String cmid =
                    UUID.randomUUID().toString().replace("-", "").substring(0, 20);

            // 5. 비즈뿌리오 INSERT
            bizMsgService.sendSms(
                    cmid,
                    order.getCustomerPhone(),
                    order.getCustomerName(),
                    "025118691",      // 발신번호 (비즈뿌리오 등록번호)
                    "윈앤티켓",        // 발신자명
                    message
            );
        }
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

