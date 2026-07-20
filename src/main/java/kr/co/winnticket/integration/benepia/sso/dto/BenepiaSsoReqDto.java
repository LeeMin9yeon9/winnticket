package kr.co.winnticket.integration.benepia.sso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(title = "[encParam 수신용] BenepiaSsoReqDto")
public class BenepiaSsoReqDto {

    @Schema(description = "고객사코드 = z381")
    private String custCoCd;

    @Schema(description = "SSO 인증토큰 = test용 : 6ec5212a76b7e632c9da50ef2e778166c425e602ee6f3c60ce79511866013fd8b29d3d61")
    private String thnKey;
}
