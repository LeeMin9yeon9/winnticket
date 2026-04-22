package kr.co.winnticket.order.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.winnticket.common.enums.OrderStatus;
import kr.co.winnticket.common.enums.PaymentMethod;
import kr.co.winnticket.common.enums.PaymentStatus;
import kr.co.winnticket.common.enums.ProductType;
import kr.co.winnticket.integration.aquaplanet.service.AquaPlanetService;
import kr.co.winnticket.integration.benepia.kcp.dto.KcpPointCancelReqDto;
import kr.co.winnticket.integration.benepia.kcp.service.KcpService;
import kr.co.winnticket.integration.benepia.order.service.BenepiaOrderService;
import kr.co.winnticket.integration.coreworks.service.CoreWorksService;
import kr.co.winnticket.integration.lscompany.service.LsCompanyService;
import kr.co.winnticket.integration.payletter.dto.PayletterCancelResDto;
import kr.co.winnticket.integration.payletter.dto.PayletterCancelResult;
import kr.co.winnticket.integration.payletter.service.PayletterService;
import kr.co.winnticket.integration.playstory.service.PlaystoryService;
import kr.co.winnticket.integration.plusn.service.PlusNService;
import kr.co.winnticket.integration.smartinfini.service.SmartInfiniService;
import kr.co.winnticket.integration.woongjin.service.WoongjinService;
import kr.co.winnticket.order.admin.dto.*;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.ticketCoupon.mapper.TicketCouponMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper mapper;
    private final PayletterService payletterService;
    private final ObjectMapper objectMapper;
    private final BenepiaOrderService benepiaOrderService;
    private final OrderPostPaymentService orderPostPaymentService;
    private final TicketCouponMapper ticketCouponMapper;

    // 파트너 연동 (취소에서 사용)
    private final WoongjinService woongjinService;
    private final PlaystoryService playstoryService;
    private final CoreWorksService coreWorksService;
    private final SmartInfiniService smartInfiniService;
    private final PlusNService plusNService;
    private final AquaPlanetService aquaplanetService;
    private final KcpService kcpService;
    private final LsCompanyService lsCompanyService;

    @Transactional(readOnly = true)
    public OrderAdminStatusGetResDto selectOrderAdminStatus() {
        OrderAdminStatusGetResDto model = mapper.selectOrderAdminStatus();
        return model;
    }

    @Transactional(readOnly = true)
    public List<OrderAdminListGetResDto> selectOrderAdminList(String asSrchWord, LocalDate asBegDate, LocalDate asEndDate, UUID partnerId, String status, UUID channelId) {
        List<OrderAdminListGetResDto> lModel = mapper.selectOrderAdminList(asSrchWord, asBegDate, asEndDate, partnerId, status, channelId);
        return lModel;
    }

    @Transactional(readOnly = true)
    public List<OrderExportResDto> selectOrderExportList(String asSrchWord, LocalDate asBegDate, LocalDate asEndDate, String status, UUID channelId, UUID partnerId) {
        return mapper.selectOrderExportList(asSrchWord, asBegDate, asEndDate, status, channelId, partnerId);
    }

    @Transactional(readOnly = true)
    public OrderAdminDetailGetResDto selectOrderAdminDetail(UUID auId) {
        OrderAdminDetailGetResDto model = mapper.selectOrderAdminDetail(auId);
        model.setProducts(mapper.selectOrderProductList(auId));
        model.setTickets(mapper.selectOrderTicketList(auId));
        return model;
    }

    @Transactional(readOnly = true)
    public OrderAdminTicketCheckGetResDto selectOrderAdminTicketList(UUID auId, UUID ticketId) {
        OrderAdminTicketCheckGetResDto model = mapper.selectOrderTicketHeader(auId);
        List<OrderTicketDetailGetResDto> tickets = mapper.selectOrderTickets(auId, ticketId);

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

    /**
     * 결제 완료 처리 — 단일 트랜잭션
     * 티켓 발급 / 파트너 연동 / 결제 확정이 하나의 트랜잭션으로 묶임
     * → 파트너 발권 실패 시 결제(PAID)도 함께 롤백되어 소비자 보호
     *
     * SMS는 트랜잭션 커밋 확정 후 비동기 발송 (afterCommit)
     */
    @Transactional
    public void completePayment(UUID auId) {
        // 주문 조회
        OrderAdminDetailGetResDto order = mapper.selectOrderAdminDetail(auId);

        if (order == null) {
            throw new IllegalArgumentException("주문이 존재하지 않습니다.");
        }

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            log.info("이미 결제 완료 skip orderId={}", auId);
            return;
        }

        // 결제 상태 / 결제일시 업데이트
        mapper.updatePaymentComplete(auId, LocalDateTime.now());

        // 입금 완료 → 입금기한 제거
        mapper.clearDepositDeadline(auId);

        log.info("입금 완료 → deadline 제거 orderId={}", auId);

        // 주문 상품 목록 조회
        List<OrderProductListGetResDto> items = mapper.selectOrderProductList(auId);

        // 티켓 발행
        Map<UUID, List<String>> ticketMap = new HashMap<>();

        for (OrderProductListGetResDto item : items) {
            UUID productId = item.getProductId();
            Boolean prePurchased = orderPostPaymentService.selectPrePurchased(productId);

            List<String> ticketNumbers = new ArrayList<>();

            Map<String, LocalDate> validity = parse(
                    item.getUsagePeriod(),
                    order.getOrderedAt().toLocalDate()
            );

            LocalDate validFrom = validity.get("from");
            LocalDate validTo = validity.get("to");

            if (Boolean.TRUE.equals(prePurchased)) {
                // 선사입: 주문 생성 시 이미 예약(PENDING)된 쿠폰을 확정(SOLD)하고 티켓 발행
                log.info("[선사입 - 예약 쿠폰 확정] orderItemId={}", item.getId());
                try {
                    List<String> issued = orderPostPaymentService.issueReservedCoupons(item.getId(), validFrom, validTo);
                    ticketNumbers.addAll(issued);
                } catch (Exception e) {
                    log.error("예약 쿠폰 확정 실패 orderItemId={}", item.getId(), e);
                    throw new RuntimeException("예약 쿠폰 확정 실패", e);
                }
            } else {
                for (int i = 0; i < item.getQuantity(); i++) {
                    log.info("[선사입 아님]");
                    String ticketNumber = orderPostPaymentService.generateTicketNumber();
                    mapper.insertOrderTicket(item.getId(), ticketNumber, validFrom, validTo);
                    ticketNumbers.add(ticketNumber);
                }
            }
            ticketMap.put(item.getId(), ticketNumbers);
        }

        log.info("[티켓] = {}", ticketMap);

        // 주문 상태 변경
        mapper.updateOrderStatus(auId);

        // 베네피아 주문 전송 (실패해도 결제는 성공 처리 - Benepia 전송은 비필수 알림)
        if (order.getBenepiaId() != null) {
            try {
                log.info("[BENEPIA 주문 전송] benefitId={}", order.getBenepiaId());
                benepiaOrderService.sendOrder(order, items);
            } catch (Exception e) {
                log.error("[BENEPIA 주문 전송 실패] 결제는 정상 처리됨. 관리자 확인 필요 orderId={}", auId, e);
            }
        }

        // 파트너 발권 (실패 시 RuntimeException → 트랜잭션 전체 롤백 → 결제도 취소)
        PartnerSplitResult split = orderPostPaymentService.splitByPartner(items);
        orderPostPaymentService.callPartnerApis(auId, order, split);

        // SMS는 커밋 확정 후 비동기 발송 (트랜잭션 롤백 시 SMS 발송 방지)
        final OrderAdminDetailGetResDto orderSnap = order;
        final Map<UUID, List<String>> ticketMapSnap = ticketMap;
        final List<OrderProductListGetResDto> itemsSnap = items;

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    log.info("[입금완료 SMS 발송] orderId={}", auId);
                    orderPostPaymentService.sendPaymentConfirmedSms(orderSnap, itemsSnap);
                } catch (Exception e) {
                    log.error("[입금완료 SMS 실패] orderId={}", auId, e);
                }

                try {
                    if (split.isHasSpavis() || split.isHasNormalProduct()
                            || split.isHasSmartInfini() || split.isHasAquaplanet()) {
                        List<OrderProductListGetResDto> normalItems =
                                orderPostPaymentService.extractNormalProducts(itemsSnap);
                        log.info("[발권완료 SMS 발송] orderId={}", auId);
                        orderPostPaymentService.sendTicketIssuedSms(orderSnap, normalItems, ticketMapSnap);
                    }
                } catch (Exception e) {
                    log.error("[발권완료 SMS 실패] orderId={}", auId, e);
                }
            }
        });
    }

    // 티켓 유효기간 추출
    public Map<String, LocalDate> parse(String text, LocalDate orderDate) {

        text = (text == null) ? "" : text.trim();

        Map<String, LocalDate> result = new HashMap<>();

        if (text == null || text.isBlank()) {
            result.put("from", null);
            result.put("to", null);
            return result;
        }

        // 숫자만 (ex: "60")
        if (text.matches("^\\d+$")) {
            int days = Integer.parseInt(text);

            result.put("from", orderDate);
            result.put("to", orderDate.plusDays(days));
            return result;
        }

        // 사용기한
        if (text.contains("사용기한")) {
            String value = text.replace("사용기한", "").trim();
            String[] arr = value.split("~");

            LocalDate from = LocalDate.parse(arr[0].trim());
            LocalDate to   = LocalDate.parse(arr[1].trim());

            result.put("from", from);
            result.put("to", to);
            return result;
        }

        // 구매로부터
        if (text.contains("구매로부터")) {
            int days = Integer.parseInt(text.replaceAll("[^0-9]", ""));

            result.put("from", orderDate);
            result.put("to", orderDate.plusDays(days));
            return result;
        }

        throw new IllegalArgumentException("유효기간 파싱 실패: " + text);
    }

    // 티켓 사용 처리
    @Transactional
    public void useTicket(UUID orderId, UUID ticketId) {
        LocalDate today = LocalDate.now();
        OrderTicketPeriod orderTicketPeriod= mapper.selectTicketUsePeriod(ticketId);

        if (orderTicketPeriod != null && today.isBefore(orderTicketPeriod.getValidFrom())) {
            throw new RuntimeException("사용 시작 전 티켓입니다.");
        }

        if (orderTicketPeriod != null && today.isAfter(orderTicketPeriod.getValidTo())) {
            throw new RuntimeException("만료된 티켓입니다.");
        }

        // 티켓 사용 처리
        int updated = mapper.updateTicketUsed(ticketId);

        if (updated == 0) {
            throw new IllegalStateException("이미 사용/취소된 티켓이거나 존재하지 않습니다.");
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
    public void cancelOrder(UUID orderId) throws Exception {

        // 주문 조회
        OrderAdminDetailGetResDto order = mapper.selectOrderAdminDetail(orderId);
        if (order == null) {
            throw new IllegalArgumentException("주문 정보가 존재하지 않습니다.");
        }

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }

        if (order.getPaymentStatus() == PaymentStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }

        // 입금 전(READY) 주문 취소 - 단순 플로우
        if (order.getPaymentStatus() == PaymentStatus.READY) {
            cancelPendingOrder(orderId, order);
            return;
        }

        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("결제 완료 또는 입금 전 주문만 취소할 수 있습니다.");
        }

        // 사용된 티켓 확인
        int usedTicketCount = mapper.countUsedTickets(orderId);
        if (usedTicketCount > 0) {
            throw new IllegalStateException("사용된 티켓이 포함된 주문은 취소할 수 없습니다.");
        }

        // 상품 조회
        List<OrderProductListGetResDto> items = mapper.selectOrderProductList(orderId);

        PartnerSplitResult split = orderPostPaymentService.splitByPartner(items);

        /*
         * =========================
         * 1. 파트너 취소 먼저
         * =========================
         */

        if (split.isHasPlusN()) {
            try {
                log.info("[플러스앤 orderCancel start]");
                plusNService.cancel(orderId);
            } catch (Exception e) {
                throw new IllegalStateException("플러스앤 주문 취소 실패", e);
            }
        }

        if (split.isHasSmartInfini()) {
            try {
                log.info("[스마트인피니 orderCancel start]");
                smartInfiniService.cancelMulti(orderId);
            } catch (Exception e) {
                throw new IllegalStateException("스마트인피니 주문 취소 실패", e);
            }
        }

        if (split.isHasLsCompany()){
            try {
                log.info("[LS컴퍼니 orderCancel start]");
                lsCompanyService.cancelTicket(orderId);
            } catch (Exception e) {
                throw new IllegalStateException("LS컴퍼니 주문 취소 실패", e);
            }
        }

        if (split.isHasAquaplanet()) {
            try {
                log.info("[아쿠아플라넷 orderCancel start]");
                aquaplanetService.cancelOrder(orderId);
            } catch (Exception e) {
                throw new IllegalStateException("아쿠아플라넷 주문 취소 실패", e);
            }
        }

        if (split.isHasWoongin()) {
            try {
                log.info("[웅진 취소 시작]");
                woongjinService.cancel(orderId);
            } catch (Exception e) {
                throw new IllegalStateException("웅진 주문 취소 실패", e);
            }
        }

        if (split.isHasPlaystory()) {
            try {
                log.info("[플레이스토리 취소 시작]");
                playstoryService.cancel(orderId);
            } catch (Exception e) {
                throw new IllegalStateException("플레이스토리 주문 취소 실패", e);
            }
        }

        /*
        if (split.isHasCoreworks()) {
            try {
                log.info("[코어웍스 취소 시작]");
                coreWorksService.cancel(orderId);
            } catch (Exception e) {
                throw new IllegalStateException("코어웍스 주문 취소 실패", e);
            }
        }

         */

        /*
         * =========================
         * 2. PG 취소
         * =========================
         */

        PaymentMethod method = order.getPaymentMethod();
        PayletterCancelResDto cancel = null;
        int cancelAmount = 0;
        int cancelFee = 0;

       // log.info("[CANCEL POLICY] amount={}, fee={}", cancelAmount, cancelFee);

        if (method == PaymentMethod.VIRTUAL_ACCOUNT) {
            if (order.getPointAmount() != null && order.getPointAmount() > 0) {

                    String tno = mapper.selectPointTno(order.getOrderNumber());

                if (tno != null) {

                    KcpPointCancelReqDto dto = new KcpPointCancelReqDto();
                    dto.setTno(tno);
                    dto.setModType("STSC");
                    dto.setCancelReason("무통장 취소");

                    try {
                        kcpService.cancelPoint(dto);
                        log.info("[POINT RETURN] 무통장 포인트 반환 완료 orderId={}", orderId);
                    } catch (Exception e) {
                        log.error("[POINT RETURN FAIL - VA] orderId={}", orderId, e);
                    }

                } else {
                    log.warn("[POINT SKIP] tno 없음 orderId={}", orderId);
                }
                }

        } else if (method == PaymentMethod.CARD || method == PaymentMethod.KAKAOPAY) {

            // 카드 취소 실행 (여기서 카드금액 - 총수수료 계산됨)
            PayletterCancelResult result = payletterService.cancel(orderId);

            cancel = result.getPgResult();
            cancelAmount = result.getCancelAmount(); // 카드 환불액
            cancelFee = result.getCancelFee();      // 수수료

            log.info("[PG CANCEL RESULT] {}", cancel);

            // 혼합결제 포인트 반환
            if (order.getPointAmount() != null && order.getPointAmount() > 0) {

                String tno = mapper.selectPointTno(order.getOrderNumber());

                if (tno != null) {

                    KcpPointCancelReqDto dto = new KcpPointCancelReqDto();

                    dto.setTno(tno);
                    dto.setCancelReason("혼합결제 취소(포인트 전액반환)");
                    dto.setModType("STSC");

                    try {
                        kcpService.cancelPoint(dto);
                        log.info("[POINT RETURN] 혼합결제 포인트 반환 완료 orderId={}", orderId);
                    } catch (Exception e) {
                        log.error("[POINT RETURN FAIL] orderId={} → 보정 필요", orderId, e);
                    }
                }else {
                    log.warn("[POINT SKIP] tno 없음 orderId={}", orderId);
                }
            }

        } else if (method == PaymentMethod.POINT) {
            String tno = mapper.selectPointTno(order.getOrderNumber());

            if (tno == null) {
                throw new IllegalStateException("포인트 거래번호(tno)가 존재하지 않습니다.");
            }

            int finalPrice = order.getFinalPrice();

            // ===== 수수료 계산 =====
            LocalDateTime orderedAt = order.getOrderedAt();

            long days = java.time.temporal.ChronoUnit.DAYS.between(
                    orderedAt.toLocalDate(),
                    LocalDate.now()
            );

            cancelFee = (days <= 7)
                    ? 1000
                    : (int) Math.floor(finalPrice * 0.1);

            int refundAmount = Math.max(finalPrice - cancelFee, 0);

            log.info("[POINT CANCEL] total={}, fee={}, refund={}", finalPrice, cancelFee, refundAmount);

            KcpPointCancelReqDto dto = new KcpPointCancelReqDto();
            dto.setTno(tno);
            dto.setCancelReason("고객요청 관리자 취소");

            if (cancelFee > 0) {
                // 부분취소 (수수료 제외 금액만 환불)
                dto.setModType("STRA");
                dto.setModMny(refundAmount);
                dto.setModOrdrIdxx(order.getOrderNumber());
                dto.setModOrdrGoods("수수료 제외 포인트 취소");
            } else {
                //  전액취소
                dto.setModType("STSC");
            }


            try {
                kcpService.cancelPoint(dto);
            } catch (Exception e) {
                log.error("[POINT CANCEL FAIL] orderId={}", orderId, e);
            }

            cancelAmount = refundAmount;
            cancelFee = cancelFee;

        } else {
            throw new IllegalArgumentException("지원하지 않는 결제수단입니다. method=" + method);
        }

        /*
         * =========================
         * 3. DB 상태 변경
         * =========================
         */

        String payloadJson = cancel != null ? objectMapper.writeValueAsString(cancel) : null;

        int updated = mapper.updateOrderCancelSuccess(
                orderId,
                cancelAmount,
                cancelFee,
                payloadJson
        );

        if (updated != 1) {
            throw new IllegalStateException("주문 취소 상태 변경 실패");
        }
        // 베네피아 주문 취소 전송
        try {
            if (order.getBenepiaId() != null) {
                log.info("[BENEPIA 주문 취소 전송] benefitId={}", order.getBenepiaId());
                benepiaOrderService.cancelOrder(order, items);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("[BENEPIA 주문 취소 전송 실패]", e);
        }

        // 재고 복구
        List<OrderItemOptionDto> options = mapper.selectOrderItemOptions(orderId);

        for (OrderItemOptionDto opt : options) {
            if (!ProductType.STAY.equals(opt.getProductType())) {
                mapper.increaseStock(opt.getOptionValueId(), opt.getQuantity());
            }
        }

        log.info("[STOCK RESTORE] 완료 orderId={}", orderId);

        // 선사입 쿠폰 복구
        List<UUID> couponIds = mapper.selectPrePurchasedCouponIds(orderId);

        for (UUID couponId : couponIds) {
            int restored = ticketCouponMapper.restoreCoupon(couponId);

            if (restored == 0) {
                throw new IllegalStateException("쿠폰 복구 실패 (이미 사용되었거나 상태 이상)");
            }
        }

        log.info("[COUPON RESTORE] 완료 orderId={}", orderId);

        mapper.cancelTicketsByOrderId(orderId);

        log.info("[ORDER_TICKETS DELETE] 완료 orderId={}", orderId);



        /*
         * =========================
         * 4. 문자 발송 (트랜잭션 커밋 후 비동기)
         * =========================
         * 취소 트랜잭션이 확실히 커밋된 후 SMS를 발송해야
         * 롤백 시 "취소완료" 문자가 잘못 발송되는 것을 방지
         */
        final OrderAdminDetailGetResDto orderForSms = order;
        final List<OrderProductListGetResDto> itemsForSms = new ArrayList<>(items);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    orderPostPaymentService.sendOrderCancelledSms(orderForSms, itemsForSms);
                } catch (Exception e) {
                    log.error("[취소 SMS 발송 실패] orderId={}", orderId, e);
                }
            }
        });

        log.info("[ORDER_CANCEL] 관리자 취소 완료 orderId={}, paymentMethod={}", orderId, method);
    }

    // 입금 전 주문 취소 (READY 상태)
    private void cancelPendingOrder(UUID orderId, OrderAdminDetailGetResDto order) throws Exception {

        // 포인트 사용 시 반환
        if (order.getPointAmount() != null && order.getPointAmount() > 0) {

            String tno = mapper.selectPointTno(order.getOrderNumber());

            if (tno != null) {
                KcpPointCancelReqDto dto = new KcpPointCancelReqDto();
                dto.setTno(tno);
                dto.setCancelReason("입금 전 주문 취소");

                kcpService.cancelPoint(dto);

                log.info("[POINT RETURN] 입금 전 주문 포인트 반환 완료 orderId={}", orderId);
            }
        }

        // 주문 상태 CANCELED 변경
        int updated = mapper.updateOrderCancelSuccess(orderId, 0, 0, null);

        if (updated != 1) {
            throw new IllegalStateException("주문 취소 상태 변경 실패");
        }

        // 재고 복구 (주문 생성 시 차감된 재고)
        List<OrderItemOptionDto> options = mapper.selectOrderItemOptions(orderId);

        for (OrderItemOptionDto opt : options) {
            if (!ProductType.STAY.equals(opt.getProductType())) {
                mapper.increaseStock(opt.getOptionValueId(), opt.getQuantity());
            }
        }

        log.info("[STOCK RESTORE] 입금 전 주문 재고 복구 완료 orderId={}", orderId);

        // 선사입 예약 쿠폰 복구 (PENDING → ACTIVE) + order_item_coupons 삭제
        orderPostPaymentService.restoreReservedCoupons(orderId);

        mapper.cancelTicketsByOrderId(orderId);

        // SMS 발송 (커밋 후 비동기)
        final OrderAdminDetailGetResDto orderForSms = order;
        final List<OrderProductListGetResDto> itemsForSms = mapper.selectOrderProductList(orderId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    orderPostPaymentService.sendOrderCancelledSms(orderForSms, itemsForSms);
                } catch (Exception e) {
                    log.error("[취소 SMS 발송 실패] orderId={}", orderId, e);
                }
            }
        });

        log.info("[ORDER_CANCEL] 입금 전 주문 관리자 취소 완료 orderId={}", orderId);
    }

    /*
    @Transactional
    public void completePaymentByOrderNumber(String orderNumber) {

        if (orderNumber == null || orderNumber.isBlank()) {
            throw new IllegalArgumentException("orderNumber empty");
        }

        //  orderNumber → orderId 조회
        UUID orderId = mapper.findOrderIdByOrderNumber(orderNumber);

        if (orderId == null) {
            throw new IllegalStateException("주문 없음 orderNumber=" + orderNumber);
        }

        //  기존 결제 완료 로직 호출
        completePayment(orderId);
    }
     */

    // 문자 재전송
    @Transactional(readOnly = true)
    public void resendTicketSms(UUID orderId) {
        // 주문 조회
        OrderAdminDetailGetResDto order = mapper.selectOrderAdminDetail(orderId);
        if (order == null) {
            throw new IllegalArgumentException("주문이 존재하지 않습니다.");
        }

        // 결제 완료만 재전송
        if (order.getPaymentStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("결제 완료된 주문만 재전송할 수 있습니다.");
        } else if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("주문 완료된 주문만 재전송할 수 있습니다.");
        }

        // 상품 조회
        List<OrderProductListGetResDto> items = mapper.selectOrderProductList(orderId);

        // 티켓 조회
        List<OrderTicketListGetResDto> tickets = mapper.selectOrderTicketList(orderId);

        // ticketMap 생성
        Map<UUID, List<String>> ticketMap = new HashMap<>();
        for (OrderTicketListGetResDto ticket : tickets) {
            ticketMap
                    .computeIfAbsent(ticket.getOrderItemId(), k -> new ArrayList<>())
                    .add(ticket.getTicketNumber());
        }

        // OrderPostPaymentService로 위임 (SMS는 비동기)
        orderPostPaymentService.resendTicketSms(orderId, order, items, ticketMap);

        log.info("[TICKET_SMS_RESEND] orderId={}", orderId);
    }
}

