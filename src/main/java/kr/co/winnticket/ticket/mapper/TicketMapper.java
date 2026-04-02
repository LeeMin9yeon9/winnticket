package kr.co.winnticket.ticket.mapper;

import kr.co.winnticket.integration.lscompany.dto.LsOrderTicket;
import kr.co.winnticket.integration.smartinfini.dto.SmartInfiniOrderTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper
public interface TicketMapper {

    // 플러스앤 쿠폰 사용처리
    int updateTicketUsed(
            @Param("orderSales") String orderSales,
            @Param("resultDate") String resultDate
    );

    // 스파비스 쿠폰조회
    List<UUID> findUnusedSpavisCoupons();

    // 스파비스 쿠폰 사용처리
    void updateSpavisTicketUsed(
            @Param("couponNo") String couponNo,
            @Param("resultDate") String resultDate
    );

    // 플레이스토리 주문조회
    List<UUID> selectPlaystoryCheckOrders();

    // 플레이스토리 쿠폰 사용처리
    int updatePlaystoryTicketUsed(
            @Param("couponNo") String couponNo,
            @Param("resultDate") String resultDate
    );

    // 웅진 주문 조회
    List<String> selectWoongjinOrders();

    // 웅진 쿠폰 사용처리
    void updateWoongjinTicketUsed(
            @Param("couponNo") String couponNo
    );

    // 스마트인피니 쿠폰 조회
    SmartInfiniOrderTicket findByTicketCodeSmartInfini(
            @Param("ticketCode") String ticketCode
    );

    // 스마트인피니 쿠폰 사용처리
    int useTicketSmartInfini(
            @Param("orderDiv") String orderDiv,
            @Param("ticketCode") String ticketCode,
            @Param("resultDate") String resultDate
    );

    // LS 컴퍼니 쿠폰 조회
    LsOrderTicket findByTicketCodeLs(
            @Param("ticketCode") String transactionId
    );

    // LS 컴퍼니 쿠폰 사용처리
    int useTicketLs(
            @Param("ticketCode") String transactionId,
            @Param("orderDiv") String code,
            @Param("resultDate") String date);
}