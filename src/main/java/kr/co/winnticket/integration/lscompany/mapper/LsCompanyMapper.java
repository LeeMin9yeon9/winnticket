package kr.co.winnticket.integration.lscompany.mapper;

import kr.co.winnticket.integration.lscompany.dto.LsOrderInfoDto;
import kr.co.winnticket.integration.lscompany.dto.LsOrderItemInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface LsCompanyMapper {


    // 주문 기본정보 조회
    LsOrderInfoDto selectOrderInfo(
            @Param("orderNumber") String orderNumber
    );

    // 주문아이템 조회
    List<LsOrderItemInfoDto> selectOrderItemInfos(
            @Param("orderId") UUID orderId
    );

    // 발권 성공 시 LS 바코드 저장
    void updatePartnerBarcode(
            @Param("barcode") String barcode,
            @Param("transactionId") String transactionId
    );

    // 여러장 주문시 발권안된 티켓 수 조회
    List<String> selectOrderTicketNumbers(
            @Param("orderItemId") UUID orderItemId
    );

    // 티켓기준 티켓번호 조회 (취소용)
    List<String> selectTicketNumbersByOrderId(@Param("orderId") UUID orderId);

    // 티켓기준 주문정보 조회 (재전송용)
    LsOrderInfoDto selectOrderInfoByOrderId(@Param("orderId") UUID orderId);

}
