package kr.co.winnticket.partners.partnerstats.mapper;

import kr.co.winnticket.partners.partnerstats.dto.PartnerCategorySalesDto;
import kr.co.winnticket.partners.partnerstats.dto.PartnerDailySalesDto;
import kr.co.winnticket.partners.partnerstats.dto.PartnerTopProductsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PartnerStatsMapper {

    // 파트너 판매현황
    Long getTotalRevenue(
            @Param("partnerId") String partnerId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    Integer getTotalOrders(
            @Param("partnerId") String partnerId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    Integer getTotalTickets(
            @Param("partnerId") String partnerId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );


    // 파트너 일별 매출
    List<PartnerDailySalesDto> getDailySales(
            @Param("partnerId") String partnerId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    // 파트너 카테고리별 매출
    List<PartnerCategorySalesDto> getCategorySales(
            @Param("partnerId") String partnerId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    // 상위 판매 상품
    List<PartnerTopProductsDto> getTopProducts(
            @Param("partnerId") String partnerId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

}
