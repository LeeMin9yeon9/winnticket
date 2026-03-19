package kr.co.winnticket.integration.benepia.order.mapper;

import kr.co.winnticket.order.admin.dto.OrderAdminDetailGetResDto;
import kr.co.winnticket.order.admin.dto.OrderProductListGetResDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Mapper
public interface BenepiaOrderBatchMapper {
    // 전일 주문 (베네피아 채널만)
    List<OrderAdminDetailGetResDto> selectBatchOrders(LocalDate targetDate);

    // 전일 취소
    List<OrderAdminDetailGetResDto> selectBatchCancels(LocalDate targetDate);

    // 주문별 상품
    List<OrderProductListGetResDto> selectOrderItems(UUID orderId);
}