package kr.co.winnticket.order.admin.service;

import kr.co.winnticket.order.admin.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 파트너 취소 진행상태 기록.
 * 주문 취소(OrderService.cancelOrder)는 파트너 취소가 하나라도 실패하면 예외로 전체 롤백되는데,
 * 이미 성공한 파트너의 외부 취소는 되돌릴 수 없고 실패한 파트너 티켓은 관리자가 알아야 하므로
 * 이 기록만큼은 롤백되지 않도록 별도 트랜잭션(REQUIRES_NEW)으로 저장한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCancelProgressRecorder {

    private final OrderMapper mapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(UUID orderId, List<String> canceledPartnerIds, String failedPartnerId, String reason) {
        for (String partnerId : canceledPartnerIds) {
            mapper.updateTicketsCancelledByPartner(orderId, partnerId);
        }
        String trimmed = reason == null ? "취소 실패"
                : (reason.length() > 500 ? reason.substring(0, 500) : reason);
        mapper.updateTicketsCancelFailed(orderId, failedPartnerId, trimmed);
    }
}
