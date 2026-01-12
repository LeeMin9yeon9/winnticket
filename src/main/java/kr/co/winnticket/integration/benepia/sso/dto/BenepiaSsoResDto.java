package kr.co.winnticket.integration.benepia.sso.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(title = "[sso 응답 DTO] BenepiaSsoRes")
public class BenepiaSsoResDto {

    @Schema(description = "베네피아 SSO 유입여부")
    private Boolean success;

    @Schema(description = "주문에서 사용하는 사용자 정보")
    private BenepiaSsoUserDto user;
}
