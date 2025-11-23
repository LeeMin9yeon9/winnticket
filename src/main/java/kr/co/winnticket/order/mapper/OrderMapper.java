package kr.co.winnticket.order.mapper;

import kr.co.winnticket.order.dto.OrderStatusCountGetResDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    // 주문 상태별 카운트 조회
    OrderStatusCountGetResDto selectOrderStatusCount();
}
