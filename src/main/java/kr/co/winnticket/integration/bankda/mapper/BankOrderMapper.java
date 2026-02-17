package kr.co.winnticket.integration.bankda.mapper;

import kr.co.winnticket.integration.bankda.dto.BankOrderDetailResponse;
import kr.co.winnticket.integration.bankda.dto.BankOrderResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper
public interface BankOrderMapper {

    // 미입금 주문 조회
    List<BankOrderResponse.Order> selectBankdaOrders();

    // 주문번호로 단건 조회 (상세용)
    BankOrderDetailResponse.Order selectBankOrderDetail(String orderId);

    // 입금완료 처리 전 상태 확인
    String selectOrderStatus(String orderId);

    // 입금완료 처리
    void updateOrderToPaid(String orderId);

    // 주문번호로 주문 id 찾기
    UUID findOrderIdByOrderNumber(String orderNumber);
}
