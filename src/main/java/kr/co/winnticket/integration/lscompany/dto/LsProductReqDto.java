package kr.co.winnticket.integration.lscompany.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[LS컴퍼니 상품 조회 요청 DTO] LsProductReqDto")

public class LsProductReqDto {

    @Schema(description = "요청데이터")
    private Data data;

    @lombok.Data
    public static class Data{

        @Schema(description = "업체코드",example = "CH2603091")
        private String agentNo;

        @Schema(description = "조회타입" ,example = "all:전체값 / single:개별")
        private String type;

        @JsonProperty("product_code")
        @Schema(description = "LS 상품코드" , example = "조회타입 single시 필수")
        private String productCode;
    }
}
