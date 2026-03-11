package kr.co.winnticket.integration.lscompany.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[LS컴퍼니 티켓 재전송 요청 DTO] LsResendReqDto]")
public class LsResendReqDto {
    private Data data;

    @lombok.Data
    public static class Data {

        @Schema(description = "업체코드")
        private String agentNo;

        @Schema(description = "윈앤티켓 주문번호")
        private String orderNo;

    }
}
