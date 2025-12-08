package kr.co.winnticket.partners.partnerstats.mapper;

import kr.co.winnticket.partners.partnerstats.dto.PartnerCategorySalesDto;
import kr.co.winnticket.partners.partnerstats.dto.PartnerDailySalesDto;
import kr.co.winnticket.partners.partnerstats.dto.PartnerStatsSummaryDto;
import kr.co.winnticket.partners.partnerstats.dto.PartnerTopProductsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PartnerStatsMapper {

    PartnerStatsSummaryDto getSummary(
            @Param("partnerId") String partnerId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    List<PartnerDailySalesDto> getDailySales(
            @Param("partnerId") String partnerId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    List<PartnerCategorySalesDto> getCategorySales(
            @Param("partnerId") String partnerId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    List<PartnerTopProductsDto> getTopProducts(
            @Param("partnerId") String partnerId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

}
