package kr.co.winnticket.integration.plusn.mapper;

import kr.co.winnticket.integration.plusn.dto.PlusNCancelRequest;
import kr.co.winnticket.integration.plusn.dto.PlusNOrderRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper
public interface PlusNMapper {
    // 주문
    PlusNOrderRequest selectPlusNOrderBase(UUID orderId);

    // 취소
    List<PlusNCancelRequest> selectPlusNCancel(UUID orderId);
}
