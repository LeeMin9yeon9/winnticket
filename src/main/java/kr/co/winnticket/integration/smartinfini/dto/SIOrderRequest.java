package kr.co.winnticket.integration.smartinfini.dto;

import kr.co.winnticket.integration.coreworks.dto.CWCancelRequest;
import lombok.Data;

import java.util.List;

@Data
public class SIOrderRequest {
    private String orderNo; // 주문번호
    private String userName; // 구매자명
    private String userHp; // 구매자핸드폰
    private String userEmail; // 구매자이메일
    private String orderDate; // 주문일자
    private String channelCode; // 채널코드 (36)
    private List<Class> classDiv;

    @Data
    public static class Class {
        private String ticketCode; // 티켓번호
        private String goodsCode; // 상품코드
        private String rstartDate; // 예약시작일 (날짜지정형 사용)
        private String rendDate; // 예약종료일 (날짜지정형 사용)
        private String barcode; // 채널지정바코드 (채널지정바코드 사용시에만)
    }

}
