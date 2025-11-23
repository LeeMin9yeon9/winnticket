package kr.co.winnticket.order.admin.mapper;

import kr.co.winnticket.order.admin.dto.OrderAdminListGetResDto;
import kr.co.winnticket.order.admin.dto.OrderAdminStatusGetResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface OrderMapper {
    // 주문 상태 조회
    OrderAdminStatusGetResDto selectOrderAdminStatus();

    // 주문 목록 조회(관리자)
    List<OrderAdminListGetResDto> selectOrderAdminList(
            @Param("srchWord") String asSrchWord,
            @Param("begDate") LocalDate asBegDate,
            @Param("endDate") LocalDate asEndDate,
            @Param("status") String status
    );
}
