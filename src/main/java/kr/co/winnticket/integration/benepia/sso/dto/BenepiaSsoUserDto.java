package kr.co.winnticket.integration.benepia.sso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(title = "[복호화결과 사용자 dto] BenepiaSsoUserDto")
public class BenepiaSsoUserDto {


    @Schema(description = "베네피아 고객 사번")
    private String userId;

    @Schema(description = "사용자명")
    private String userName;


}
