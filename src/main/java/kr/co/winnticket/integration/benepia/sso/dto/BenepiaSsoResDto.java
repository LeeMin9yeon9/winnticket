package kr.co.winnticket.integration.benepia.sso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[sso 응답 DTO] BenepiaSsoRes")
public class BenepiaSsoResDto {

    @Schema(description = "베네피아 SSO 유입여부")
    private String responseCode;

    @Schema(description = "주문에서 사용하는 사용자 정보")
    private String responseMessage;
}
