package kr.co.winnticket.order.admin.mapper;

import kr.co.winnticket.order.admin.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface OrderMapper {
    // 주문 상태 조회
    OrderAdminStatusGetResDto selectOrderAdminStatus();

    // 주문 목록 조회(관리자)
    List<OrderAdminListGetResDto> selectOrderAdminList(
            @Param("srchWord") String asSrchWord,
            @Param("begDate") LocalDate asBegDate,
            @Param("endDate") LocalDate asEndDate,
            @Param("status") String status
    );

    // 주문 상세 조회(관리자)
    OrderAdminDetailGetResDto selectOrderAdminDetail(
            @Param("id") UUID auId
    );


    List<OrderProductListGetResDto> selectOrderProductList(
            @Param("id") UUID auId
    );

    List<OrderTicketListGetResDto> selectOrderTicketList(
            @Param("id") UUID auId
    );

    // 주문 결제 완료 처리
    int updatePaymentComplete(
            @Param("id") UUID orderId,
            @Param("paidAt") LocalDateTime paidAt
    );

    // 주문 상태 변경
    int updateOrderStatus(
            @Param("id") UUID orderId
    );

    // 주문 상품 목록 조회 (티켓 발행용)
    List<OrderProductListGetResDto> selectOrderItemsForTicket(
            @Param("id") UUID orderId
    );

    // 티켓 발행
    void insertOrderTicket(
            @Param("id") UUID orderId,
            @Param("orderItemId") UUID orderItemId,
            @Param("ticketNumber") String ticketNumber
    );

    // 주문 + 티켓 헤더 조회
    OrderAdminTicketCheckGetResDto selectOrderTicketHeader(
            @Param("id") UUID auId
    );

    // 주문에 속한 티켓 목록 조회
    List<OrderTicketDetailGetResDto> selectOrderTickets(
            @Param("id") UUID auId
    );

    // 티켓 사용 처리
    int updateTicketUsed(UUID ticketId);

    // 주문 내 미사용 티켓 수
    int countUnusedTickets(UUID orderId);

    // 주문 상태 변경 (모두 사용 완료)
    void updateOrderCompleted(UUID orderId);
}
