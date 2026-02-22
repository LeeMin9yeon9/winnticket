package kr.co.winnticket.integration.smartinfini.mapper;

import kr.co.winnticket.integration.smartinfini.dto.SICancelListRequest;
import kr.co.winnticket.integration.smartinfini.dto.SIMmsResendRequest;
import kr.co.winnticket.integration.smartinfini.dto.SIOrderRequest;
import kr.co.winnticket.integration.smartinfini.dto.SIOrderSearchRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.UUID;

@Mapper
public interface SmartInfiniMapper {

    // 주문
    SIOrderRequest selectSmartinfiniOrder(UUID orderId);

    // 조회(다건)
    SIOrderSearchRequest selectSmartinfinisearchByOrderNo(UUID orderId);

    // 문자 재전송
    SIMmsResendRequest selectSmartinfiniMmsResend(UUID orderId);

    // 주문취소(다건)
    SICancelListRequest selectSmartinfiniCancelList(UUID orderId);
}
