package kr.co.winnticket.partners.partnerstats.service;

import kr.co.winnticket.partners.partnerstats.dto.*;
import kr.co.winnticket.partners.partnerstats.mapper.PartnerStatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerStatsService {

    private final PartnerStatsMapper mapper;

    //파트너 통계 전체
    public PartnerStatsResDto getPartnerStats(String partnerId, String startDate, String endDate){

        PartnerStatsResDto resDto = new PartnerStatsResDto();
        PartnerStatsSummaryDto summaryDto = new PartnerStatsSummaryDto();

        // 총 매출
        Long totalRevenue = mapper.getTotalRevenue(partnerId,startDate,endDate);
        // 총 주문
        Integer totalOrders = mapper.getTotalOrders(partnerId,startDate,endDate);
        // 총 판매티켓
        Integer tatalTickets = mapper.getTotalTickets(partnerId,startDate,endDate);

        summaryDto.setTotalRevenue(totalRevenue);
        summaryDto.setTotalOrders(totalOrders);
        summaryDto.setTotalTickets(tatalTickets);

        summaryDto.setAverageOrderValue(
                (totalOrders == 0) ? 0 : (int)(totalRevenue / totalOrders));

        resDto.setSummary(summaryDto);

        // 일별 매출
        List<PartnerDailySalesDto> dailySales =
                mapper.getDailySales(partnerId, startDate, endDate);
        resDto.setDailySales(dailySales);

        // 카테고리 매출
        List<PartnerCategorySalesDto> categorySales =
                mapper.getCategorySales(partnerId, startDate, endDate);
        resDto.setCategorySales(categorySales);

        // 상위 판매 상품
        List<PartnerTopProductsDto> topProducts =
                mapper.getTopProducts(partnerId, startDate, endDate);
        resDto.setTopProducts(topProducts);

        return resDto;
    }
    }
