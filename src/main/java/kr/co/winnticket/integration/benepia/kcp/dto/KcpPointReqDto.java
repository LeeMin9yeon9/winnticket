package kr.co.winnticket.integration.benepia.kcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "[베네피아 KCP 포인트조회 요청 DTO]KcpPointReqDto")
public class KcpPointReqDto {

//    @NotNull
//    @Schema(description = "주문번호")
//    private String orderNo;

    @NotNull
    @Schema(description = "결제금액")
    private Integer amount;

    @NotNull
    @Schema(description = "베네피아 아이디")
    private String benepiaId;

    @NotNull
    @Schema(description = "베네피아 비밀번호")
    private String benepiaPwd;

    @Schema(description = "베네피아 회원 소속사 코드",example = "테스트 시 null")
    private String memcorpCd;

//    @Schema(description = "요청자IP")
//    private String userIp;
}
