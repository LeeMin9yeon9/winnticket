package kr.co.winnticket.dashboard.mapper;

import kr.co.winnticket.dashboard.dto.DashboardCategoryDto;
import kr.co.winnticket.dashboard.dto.DashboardDailySalesDto;
import kr.co.winnticket.dashboard.dto.DashboardPartnerSalesDto;
import kr.co.winnticket.dashboard.dto.DashboardTopProductDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DashboardMapper {


    //  등록 상품 수
    int selectProductCount();

    // 판매 상품 수
    int selectOnSaleProductCount();

    // 준비중 상품 수
    int selectReadyProductCount();


    //파트너 수
    int selectPartnerCount();

    // 활성 파트너 수
    int selectActivePartnerCount();

    // 비활성 파트너 수
    int selectInactivePartnerCount();

    // 전체 주문 수
    int selectTotalOrderCount(@Param("period") String period);

    // 전체 주문 완료 수
    int selectOrderCount(@Param("period") String period);

    // 취소 주문 수
    int selectCancelOrderCount(@Param("period") String period);

    // 이번달 주문 (하위호환용 - period로 대체)
    default int selectThisMonthTotalOrderCount() { return 0; }
    default int selectThisMonthOrderCount() { return 0; }
    default int selectThisMonthCancelOrderCount() { return 0; }

    // 파트너별 매출 현황
    List<DashboardPartnerSalesDto> selectPartnerSales(@Param("period") String period);

    // 카테고리별상품
    List<DashboardCategoryDto> selectCategoryProducts();

    // 상위 판매 상품
    List<DashboardTopProductDto> selectTopProducts(@Param("period") String period);

    // 일별 판매 추이
    List<DashboardDailySalesDto> selectDailySales(@Param("period") String period);

}

