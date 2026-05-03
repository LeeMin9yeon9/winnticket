package kr.co.winnticket.integration.plusn.mapper;

import kr.co.winnticket.integration.plusn.dto.PlusNCancelRequest;
import kr.co.winnticket.integration.plusn.dto.PlusNOrderRequest;
import kr.co.winnticket.integration.plusn.dto.PlusNTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface PlusNMapper {
    // 주문
    PlusNOrderRequest selectPlusNOrderBase(UUID orderId);

    // 취소
    List<PlusNCancelRequest> selectPlusNCancel(UUID orderId);

    // 주문 후 order_id로 티켓ID 찾기
    List<PlusNTicket> selectTicketsForPlusN(UUID orderId);

    // 주문후 플러스앤 주문번호 저장
    void updateTicketOrderSales(
            @Param("ticketId") UUID ticketId,
            @Param("orderSales") String orderSales,
            @Param("couponNo") String couponNo
    );
}
