package kr.co.winnticket.integration.woongjin.mapper;

import kr.co.winnticket.integration.woongjin.dto.WJCancelRequest;
import kr.co.winnticket.integration.woongjin.dto.WJOrderRequest;
import kr.co.winnticket.integration.woongjin.dto.WJResendRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.UUID;

@Mapper
public interface WoongjinMapper {

    // 주문
    WJOrderRequest selectWJOrder(UUID orderId);

    // 취소
    WJCancelRequest selectWJCancel(UUID orderId);

    // 재전송
    WJResendRequest selectWJResend(UUID orderId);
}
