package kr.co.winnticket.integration.mair.mapper;

import kr.co.winnticket.integration.mair.dto.MairOrderInfoDto;
import kr.co.winnticket.integration.mair.dto.MairOrderItemInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface MairOrderMapper {

    // 주문 조회
    MairOrderInfoDto selectOrderInfo(
            @Param("orderNumber") String orderNumber
    );

    // orderID로 주문아이템 목록 조회
    List<MairOrderItemInfoDto> selectOrderItemInfos(
            @Param("orderId") UUID orderId
    );

    // 티켓 저장
    int insertOrderTicket(
            @Param("orderItemId") String orderItemId,
            @Param("ticketNumber") String ticketNumber);

    // 티켓 저장(ticketNo 나중에 지우기 )
//    int insertOrderTicket(
//            @Param("orderItemId") String orderItemId,
//            @Param("ticketNumber") String ticketNumber,
//            String ticketNo);

    // 티켓 중복 발송 방지
    int countOrderTickets(@Param("orderItemId") UUID orderItemId);

    // 티켓번호 목록 조회
    List<String> selectTicketNumbers(@Param("orderItemId") UUID orderItemId);

    // 취소 가능한 티켓 조회
    int countCancelableTickets(@Param("orderItemId") UUID orderItemId);

    // 티켓 취소 처리
    int updateTicketsCanceled(UUID orderItemId);
}

