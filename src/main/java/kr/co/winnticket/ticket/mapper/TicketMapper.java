package kr.co.winnticket.ticket.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TicketMapper {

    int updateTicketUsed(
            @Param("partnerId") UUID partnerId,
            @Param("orderSales") String orderSales,
            @Param("resultDate") String resultDate
    );

    // 스파비스 쿠폰조회
    List<UUID> findUnusedSpavisCoupons();
}