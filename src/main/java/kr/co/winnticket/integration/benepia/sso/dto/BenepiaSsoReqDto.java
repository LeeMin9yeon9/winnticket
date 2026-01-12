package kr.co.winnticket.integration.benepia.sso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "[encParam 수신용] BenepiaSsoReqDto")
public class BenepiaSsoReqDto {

    @Schema(description = "베네피아에서 로그인 후 넘어올때 전달되는 암호화 파라미터")
    private String encParam;

    @Schema(description = "암호화 안된 returnUrl")
    private String returnUrl;
}
