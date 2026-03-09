package kr.co.winnticket.integration.lscompany.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[LS컴퍼니 시설 정보조회 요청 DTO]LsPlaceReqDto")
public class LsPlaceReqDto {
    @Schema(description = "요청 데이터")
    private Data data;

    @lombok.Data
    public static class Data {

        @Schema(description = "업체코드", example = "CH2603091")
        private String agentNo;
    }
}
