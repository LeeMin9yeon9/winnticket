package kr.co.winnticket.integration.benepia.sso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[sso 토큰 확인 DTO] BenepiaSsoResDto")
public class BenepiaTokenResDto {

    @Schema(description = "베네피아 토큰 응답코드")
    private String responseCode;

    @Schema(description = "베네피아 토큰 응답메세지")
    private String responseMessage;

    private ResponseData responseData;

    @Data
    public static class ResponseData{

        // 발급받은 토큰 키
        private String tknKey;

        private String clientIp;
    }

}
