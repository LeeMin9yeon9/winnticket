package kr.co.winnticket.integration.lscompany.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[LS컴퍼니 티켓 취소 요청 DTO] LsCancelReqDto")
public class LsCancelReqDto {

    private Data data;

    @lombok.Data
    public static class Data {

        @Schema(description = "업체코드")
        private String agentNo;

        @Schema(description = "티켓 번호")
        private String transactionId;

    }
}
