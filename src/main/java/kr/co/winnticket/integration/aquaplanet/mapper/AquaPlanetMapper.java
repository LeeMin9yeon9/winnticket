package kr.co.winnticket.integration.aquaplanet.mapper;

import kr.co.winnticket.integration.aquaplanet.dto.AquaPlanetInterfaceDTO.IssueInput;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.UUID;

@Mapper
public interface AquaPlanetMapper {
    // 발행을 위한 주문 데이터 조회
    IssueInput selectIssueData(@Param("orderId") UUID orderId);

    // 쿠폰번호 업데이트
    void updateTicketCoupon(@Param("orderId") UUID orderId, @Param("couponNo") String couponNo);
}