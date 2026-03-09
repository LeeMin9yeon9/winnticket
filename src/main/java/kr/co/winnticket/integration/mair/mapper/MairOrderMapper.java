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

    // 엠에어 쿠폰번호 저장
    void updatePartnerOrderCode(
            @Param("orderItemId") UUID orderItemId,
            @Param("tno") String tno
    );

    // 남은 티켓 개수 확인( 여러개 주문 시)
    int countRemainTickets(UUID orderItemId);



}

