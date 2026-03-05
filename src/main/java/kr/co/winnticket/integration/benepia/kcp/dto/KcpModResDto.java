package kr.co.winnticket.integration.benepia.kcp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "[KCP 취소/재승인 응답 DTO]KcpModResDto")
public class KcpModResDto {
    @Schema(description = "응답코드", example = "0000")
    private String res_cd;

    @Schema(description = "응답메시지", example = "정상처리")
    private String res_msg;

    @Schema(description = "거래번호", example = "23030112345678901234")
    private String tno;
}
