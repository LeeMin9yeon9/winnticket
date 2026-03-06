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


        // 전체 주문 수
        res.setTotalOrderCount(mapper.selectTotalOrderCount());

        // 주문 완료 수
        res.setOrderCount(mapper.selectOrderCount());

        // 주문 취소 수
        res.setCancelOrderCount(mapper.selectCancelOrderCount());


        // 이번달 주문 수
        res.setThisMonthTotalOrderCount(mapper.selectThisMonthTotalOrderCount());

        // 이번달 주문 완료 수
        res.setThisMonthOrderCount(mapper.selectThisMonthOrderCount());

        // 이번달 주문 취소 수
        res.setThisMonthCancelOrderCount(mapper.selectThisMonthCancelOrderCount());


        // 파트너 별 매출 현황
        res.setPartnerSales(mapper.selectPartnerSales());

        // 카테고리 별 상품
        res.setCategoryProducts(mapper.selectCategoryProducts());

        // 상위 판매 상품
        res.setTopProducts(mapper.selectTopProducts());

        // 일별 판매 추이
        res.setDailySales(mapper.selectDailySales(period));

        return res;
    }
}
