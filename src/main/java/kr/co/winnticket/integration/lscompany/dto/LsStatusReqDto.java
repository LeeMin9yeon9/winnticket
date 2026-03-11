package kr.co.winnticket.integration.lscompany.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[LS컴퍼니 티켓 상태조회 요청 DTO] LsStatusReqDto")
public class LsStatusReqDto {
    @Schema(description = "요청 데이터")
    private Data data;

    @lombok.Data
    public static class Data {

        @Schema(description = "업체코드")
        private String agentNo;

        @Schema(description = "티켓 고유키(transactionId)")
        private String transactionId;
    }
}
