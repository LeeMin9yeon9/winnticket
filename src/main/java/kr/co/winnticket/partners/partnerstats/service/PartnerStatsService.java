package kr.co.winnticket.partners.partnerstats.service;

import kr.co.winnticket.partners.partnerstats.dto.PartnerCategorySalesDto;
import kr.co.winnticket.partners.partnerstats.dto.PartnerDailySalesDto;
import kr.co.winnticket.partners.partnerstats.dto.PartnerStatsSummaryDto;
import kr.co.winnticket.partners.partnerstats.dto.PartnerTopProductsDto;
import kr.co.winnticket.partners.partnerstats.mapper.PartnerStatsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerStatsService {

    private final PartnerStatsMapper mapper;

    //파트너 통계 전체
    public PartnerStatsSummaryDto getSummary(String partnerId, String startDate, String endDate) {
        return mapper.getSummary(partnerId, startDate, endDate);
    }

    // 파트너 일매출
    public List<PartnerDailySalesDto> getDailySales(String partnerId, String startDate, String endDate) {
        return mapper.getDailySales(partnerId, startDate, endDate);
    }

    // 파트너 카테고리 별 매출
    public List<PartnerCategorySalesDto> getCategorySales(String partnerId, String startDate, String endDate) {
        return mapper.getCategorySales(partnerId, startDate, endDate);
    }

    // 파트너 TOP3
    public List<PartnerTopProductsDto> getTopProducts(String partnerId, String startDate, String endDate) {
        return mapper.getTopProducts(partnerId, startDate, endDate);
    }

}