package kr.co.winnticket.integration.benepia.sso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "[웹 제휴몰 복호화 후 paramDTO] BenepiaDecryptedParamDto")
public class BenepiaDecryptedParamDto {

    @Schema(description="고객사 코드")
    private String sitecode;

    @Schema(description="고객 사번")
    private String userid;

    @Schema(description="베네피아 로그인 아이디")
    private String benefit_id;

    @Schema(description="고객사 이름")
    private String sitename;

    @Schema(description="고객 이름")
    private String username;

    @Schema(description="토큰 키(72자리)")
    private String tknKey;

    @Schema(description="제휴사 returnurl(암호화 제외)")
    private String returnurl;
}
