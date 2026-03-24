package kr.co.winnticket.dashboard.service;

import kr.co.winnticket.dashboard.dto.DashboardResDto;
import kr.co.winnticket.dashboard.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardMapper mapper;

    /**
     * 관리자 대시보드 전체 조회
     */
    public DashboardResDto getDashboard(String period) {

        DashboardResDto res = new DashboardResDto();

        // 총 등록 상품
        res.setProductCount(mapper.selectProductCount());

        // 판매 상품 수
        res.setOnSaleProductCount(mapper.selectOnSaleProductCount());

        // 준비중 상품 수
        res.setReadyProductCount(mapper.selectReadyProductCount());



        // 파트너 수
        res.setPartnerCount(mapper.selectPartnerCount());

        // 활성 파트너 수
        res.setActivePartnerCount(mapper.selectActivePartnerCount());

        // 비활성 파트너 수
        res.setInactivePartnerCount(mapper.selectInactivePartnerCount());


        // 전체 주문 수 (기간 필터)
        res.setTotalOrderCount(mapper.selectTotalOrderCount(period));

        // 주문 완료 수 (기간 필터)
        res.setOrderCount(mapper.selectOrderCount(period));

        // 주문 취소 수 (기간 필터)
        res.setCancelOrderCount(mapper.selectCancelOrderCount(period));

        // 이번달 주문 수 → 기간 필터와 동일하게
        res.setThisMonthTotalOrderCount(mapper.selectTotalOrderCount(period));
        res.setThisMonthOrderCount(mapper.selectOrderCount(period));
        res.setThisMonthCancelOrderCount(mapper.selectCancelOrderCount(period));


        // 파트너 별 매출 현황
        res.setPartnerSales(mapper.selectPartnerSales(period));

        // 카테고리 별 상품
        res.setCategoryProducts(mapper.selectCategoryProducts());

        // 상위 판매 상품
        res.setTopProducts(mapper.selectTopProducts(period));

        // 일별 판매 추이
        res.setDailySales(mapper.selectDailySales(period));

        return res;
    }
}
