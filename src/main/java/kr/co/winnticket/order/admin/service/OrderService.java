package kr.co.winnticket.order.admin.service;

import jakarta.transaction.Transactional;
import kr.co.winnticket.common.enums.OrderStatus;
import kr.co.winnticket.common.enums.PaymentStatus;
import kr.co.winnticket.order.admin.dto.*;
import kr.co.winnticket.order.admin.mapper.OrderMapper;
import kr.co.winnticket.product.admin.dto.ProductOptionValueGetResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper mapper;

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
                throw new IllegalStateException("이미 결제 완료된 주문입니다.");
            }

            // 결제 상태 / 결제일시 업데이트
            mapper.updatePaymentComplete(auId, LocalDateTime.now());

            // 주문 상품 목록 조회
            List<OrderProductListGetResDto> items = mapper.selectOrderProductList(auId);

            // 티켓 발행
            for (OrderProductListGetResDto item : items) {
                for (int i = 0; i < item.getQuantity(); i++) {
                    mapper.insertOrderTicket(auId, item.getId(), generateTicketNumber(auId, item.getId()));
                }
            }

            // 주문 상태 변경
            mapper.updateOrderStatus(auId);
        } catch (Exception e) {
            log.error("주문 생성 중 오류 발생", e);
            throw e; // 다시 던짐 (중요)
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
}
