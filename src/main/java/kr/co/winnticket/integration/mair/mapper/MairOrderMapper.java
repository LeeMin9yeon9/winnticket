package kr.co.winnticket.integration.mair.mapper;

import kr.co.winnticket.integration.mair.dto.MairOrderInfoDto;
import kr.co.winnticket.integration.mair.dto.MairOrderItemInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface MairOrderMapper {

    // 주문 조회
    MairOrderInfoDto selectOrderInfo(
            @Param("orderNumber") String orderNumber
    );

    // orderID로 주문아이템 목록 조회
    List<MairOrderItemInfoDto> selectOrderItemInfos(
            @Param("orderId") UUID orderId
    );

    // 티켓 중복 발송 방지
    int countOrderTickets(@Param("orderItemId") UUID orderItemId);

    // 티켓 취소 처리
    int updateTicketsCanceled(UUID orderItemId);
}

