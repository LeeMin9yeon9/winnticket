package kr.co.winnticket.integration.coreworks.mapper;

import kr.co.winnticket.integration.coreworks.dto.CWCancelRequest;
import kr.co.winnticket.integration.coreworks.dto.CWMmsResendRequest;
import kr.co.winnticket.integration.coreworks.dto.CWOrderRequest;
import kr.co.winnticket.integration.coreworks.dto.CWSearchRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.UUID;

@Mapper
public interface CoreWorksMapper {

    // 주문
    CWOrderRequest selectCoreworksOrder(UUID orderId);

    // 티켓조회
    CWSearchRequest selectCoreworksSearch(UUID orderId);

    // 티켓취소
    CWCancelRequest selectCoreworksCancel(UUID orderId);

    // 문자 재발송
    CWMmsResendRequest selectCoreworksMmsResend(UUID orderId);
}
