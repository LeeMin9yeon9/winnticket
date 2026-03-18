package kr.co.winnticket.integration.lscompany.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "[LS컴퍼니 티켓 발권 요청 DTO] LsIssueReqDto")
public class LsIssueReqDto {

    private Data data;

    @lombok.Data
    public static class Data {

        @Schema(description = "업체코드")
        private String agentNo;

        @Schema(description = "주문자명")
        private String orderName;

        @Schema(description = "주문자휴대폰번호")
        private String orderHp;

        @Schema(description = "티켓수신자명")
        private String name;

        @Schema(description = "티켓수신자 휴대폰")
        private String hp;

        @Schema(description = "티켓수신 이메일")
        private String email;

        @Schema(description = "발권일자")
        private String date;

        @Schema(description = "발권요청번호")
        private String orderNo;

        private List<Order> order;

        @lombok.Data
        public static class Order {

            @Schema(description = "윈앤티켓 생성 티켓번호")
            private String transactionId;

            @Schema(description = "LS 옵션아이디")
            private String optionId;

            @Schema(description = "판매가")
            private String price;

            @Schema(description = "할인가")
            private String discount;

            @Schema(description = "유효기간 시작일")
            private String orderSdate;

            @Schema(description = "유효기간 종료일")
            private String orderEdate;

            private String optionType;
            private String optionName;
        }
    }
}

