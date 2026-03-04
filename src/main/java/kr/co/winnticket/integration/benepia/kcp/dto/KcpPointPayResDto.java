package kr.co.winnticket.integration.benepia.kcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "[베네피아 KCP 포인트결제 응답 DTO]KcpPointPayResDto")
public class KcpPointPayResDto {

    @Schema(description = "응답코드")
    private String res_cd;

    @Schema(description = "응답메시지")
    private String res_msg;

    @Schema(description = "거래번호 (KCP TNO)")
    private String tno;

    @Schema(description = "승인번호")
    private String app_no;

    @Schema(description = "결제금액")
    private String amount;
}
