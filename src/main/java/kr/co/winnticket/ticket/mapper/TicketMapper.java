package kr.co.winnticket.ticket.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TicketMapper {

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
}