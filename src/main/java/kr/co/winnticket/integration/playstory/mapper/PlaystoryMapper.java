package kr.co.winnticket.integration.playstory.mapper;

import kr.co.winnticket.integration.playstory.dto.PlaystoryCheckCancelRequest;
import kr.co.winnticket.integration.playstory.dto.PlaystoryCheckRequest;
import kr.co.winnticket.integration.playstory.dto.PlaystoryOrderRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.UUID;

@Mapper
public interface PlaystoryMapper {
    // 주문
    PlaystoryOrderRequest selectPlaystoryOrder(UUID orderId);

    // 사용조회
    PlaystoryCheckRequest selectPlaystoryCheck(UUID orderId);

    // 주문취소
    PlaystoryCheckCancelRequest selectPlaystoryCancel(UUID orderId);
}
