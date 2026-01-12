package kr.co.winnticket.integration.benepia.sso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[sso 토큰 생성 DTO] BebepiaTokenCreateResDto")
public class BenepiaTokenCreateResDto {
    @Schema(description = "베네피아 SSO 응답코드")
    private String responseCode;

    @Schema(description = "베네피아 SSO 응답메세지")
    private String responseMessage;

    @Schema(description = "베네피아 발급받은 Toker Key")
    private String tknKey;
}
